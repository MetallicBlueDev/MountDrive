package com.metallicbluedev.mountdrive;

import com.metallicbluedev.core.XmlSettingManager;
import com.metallicbluedev.factory.FactoryManager;
import jakarta.xml.bind.annotation.*;

/**
 *
 * @author Sébastien Villemain
 */
@XmlRootElement(name = "AppSettings")
public class MountDriveSettingManager extends XmlSettingManager<MountDriveSettings> {

    protected MountDriveSettingManager() {
        super(MountDriveSettings.class);
    }

    public static MountDriveSettingManager getInstance() {
        return FactoryManager.getInstance(MountDriveSettingManager.class);
    }

}
