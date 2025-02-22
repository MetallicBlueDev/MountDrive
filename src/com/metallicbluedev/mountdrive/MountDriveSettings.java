package com.metallicbluedev.mountdrive;

import com.metallicbluedev.dto.AppSettings;
import jakarta.xml.bind.annotation.*;
import java.util.*;

/**
 *
 * @author Sébastien Villemain
 */
@XmlRootElement(name = "AppSettings")
public class MountDriveSettings extends AppSettings {

    private int reachableRetry = 3;
    private int reachableTimeout = 5000;
    private int resetDrivesCount = 3;

    private List<Drive> drives;

    public List<Drive> getDrives() {
        return drives;
    }

    public int getReachableRetry() {
        return reachableRetry;
    }

    public int getReachableTimeout() {
        return reachableTimeout;
    }

    public int getResetDrivesCount() {
        return resetDrivesCount;
    }

    @XmlElementWrapper()
    @XmlElement(name = "drive")
    public void setDrives(List<Drive> drives) {
        this.drives = drives;
    }

    public void setReachableRetry(int reachableRetry) {
        this.reachableRetry = reachableRetry;
    }

    public void setReachableTimeout(int reachableTimeout) {
        this.reachableTimeout = reachableTimeout;
    }

    public void setResetDrivesCount(int resetDrivesCount) {
        this.resetDrivesCount = resetDrivesCount;
    }

}
