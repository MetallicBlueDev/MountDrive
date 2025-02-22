package com.metallicbluedev.mountdrive;

import com.metallicbluedev.core.DefaultMainManager;
import com.metallicbluedev.core.MainManager;
import com.metallicbluedev.factory.FactoryManager;
import com.metallicbluedev.logger.LoggerManager;

/**
 *
 * @author Sébastien Villemain
 */
public class MountDrive {

    public static void main(String[] args) {
        FactoryManager.register(DefaultMainManager.class);
        FactoryManager.register(MountDriveSettingManager.class);

        MainManager instance = MainManager.getInstance();
        instance.setCommands(args);
        instance.setMainAppClass(MountDrive.class);
        instance.addMainProcess(DriveController.class);

        try {
            instance.start();
            instance.listen();
        } catch (Exception e) {
            LoggerManager.getInstance().addError(e);
        }
    }
}
