package com.metallicbluedev.mountdrive;

import com.metallicbluedev.core.MainManager;
import com.metallicbluedev.core.GenericMainProcess;
import com.metallicbluedev.utils.NetworkHelper;
import com.metallicbluedev.utils.FileHelper;
import com.metallicbluedev.utils.ProcessHelper;
import com.metallicbluedev.logger.LoggerManager;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 *
 * @author Sébastien Villemain
 */
public class DriveController extends GenericMainProcess {

    private final List<Drive> drives;
    private final int reachableRetry;
    private final int reachableTimeout;

    private List<FileStore> fileStores = null;

    private int resetDrivesCount = 3;
    private boolean canResetDrives = false;

    public DriveController() {
        drives = MountDriveSettingManager.getInstance().getSettings().getDrives();
        reachableRetry = MountDriveSettingManager.getInstance().getSettings().getReachableRetry();
        reachableTimeout = MountDriveSettingManager.getInstance().getSettings().getReachableTimeout();
        resetDrivesCount = MountDriveSettingManager.getInstance().getSettings().getResetDrivesCount();
    }

    @Override
    protected void onRunning() {
        checkDrives();
        MainManager.getInstance().stop();
    }

    private void checkDrives() {
        if (drives != null) {
            do {
                LoggerManager.getInstance().addInformation("Checking drives...");

                if (doResetDrives()) {
                    resetDrives();
                }

                for (Drive drive : drives) {
                    waitNetwork(drive);
                    checkDrive(drive);

                    if (doResetDrives()) {
                        break;
                    }
                }
            } while (doResetDrives());
        }
    }

    private void checkDrive(Drive drive) {
        LoggerManager.getInstance().addInformation("Check drive: " + drive.getFriendlyName());

        boolean created = created(drive);

        if (drive.getNumberOfAttempts() > 0
            && !created) {
            LoggerManager.getInstance().addInformation("Wait before create drive: " + drive.getFriendlyName() + " (" + drive.getNumberOfAttempts() + "/" + resetDrivesCount + drive.getNumberOfAttempts() + ")");

            try {
                Thread.sleep(1000 * drive.getNumberOfAttempts());
            } catch (InterruptedException ex) {
                LoggerManager.getInstance().addError(ex);
            }
        }

        if (!created
            && !create(drive)) {
            LoggerManager.getInstance().addWarning("Drive not found: " + drive.getFriendlyName());
            setResetDrives(true);
        }
    }

    private boolean created(Drive drive) {
        boolean created = false;
        List<FileStore> currentStores = getFileStores();

        for (FileStore fileStore : currentStores) {
            if (match(drive, fileStore)) {
                created = true;
                break;
            }
        }
        return created;
    }

    private static boolean match(Drive drive, FileStore fileStore) {
        boolean created = false;

        if (fileStore.name().equalsIgnoreCase(drive.getName())) {
            try {
                created = !fileStore.isReadOnly() && fileStore.getTotalSpace() > 1;
            } catch (IOException ex) {
                LoggerManager.getInstance().addError(ex);
            }
        }
        return created;
    }

    public List<FileStore> getFileStores() {
        if (fileStores == null) {
            fileStores = FileHelper.getFileStores();
        }
        return fileStores;
    }

    private boolean create(Drive drive) {
        LoggerManager.getInstance().addInformation("Create drive: " + drive.getFriendlyName());

        drive.newAttempt();

        String[] command = drive.getCommand();

        if (ProcessHelper.executeCli(1000, "net.exe", command)) {
            LoggerManager.getInstance().addInformation("Create drive executed: " + drive.getFriendlyName());
            cleanFileStores();
        } else {
            LoggerManager.getInstance().addWarning("Create drive not executed: " + drive.getFriendlyName());
            setResetDrives(true);
        }
        return created(drive);
    }

    private void resetDrives() {
        LoggerManager.getInstance().addInformation("Reset all drives");

        String[] command = new String[]{
            "use",
            "/delete",
            "*",
            "/yes"
        };

        if (ProcessHelper.executeCli(1000, "net.exe", command)) {
            LoggerManager.getInstance().addInformation("Reset executed");
        } else {
            LoggerManager.getInstance().addWarning("Reset fail");
        }

        cleanFileStores();
        setResetDrives(false);
        resetDrivesCount--;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            LoggerManager.getInstance().addError(ex);
        }
    }

    private void setResetDrives(boolean reset) {
        if (reset
            && !canResetDrives) {
            LoggerManager.getInstance().addInformation("Reset all drives before next check");
        }
        canResetDrives = reset;
    }

    private boolean doResetDrives() {
        return canResetDrives && resetDrivesCount > 0;
    }

    private void cleanFileStores() {
        fileStores = null;
    }

    private void waitNetwork(Drive drive) {
        String hostName = drive.getHostName();

        if (hostName != null) {
            LoggerManager.getInstance().addInformation("Wait network: " + hostName);

            NetworkHelper.waitForHostReachable(hostName, reachableRetry, reachableTimeout);
        }
    }

}
