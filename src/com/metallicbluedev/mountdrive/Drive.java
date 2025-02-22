package com.metallicbluedev.mountdrive;

import jakarta.xml.bind.annotation.*;

/**
 *
 * @author Sébastien Villemain
 */
public class Drive {

    private String letter = null;
    private String path = null;
    private String userName = null;
    private String password = null;

    public String getLetter() {
        return letter;
    }

    @XmlAttribute()
    public void setLetter(String letter) {
        this.letter = letter.toLowerCase().trim();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }

    private int numberOfAttempts = 0;

    public void newAttempt() {
        numberOfAttempts++;
    }

    public String[] getCommand() {
        return new String[]{
            "use",
            letter + ":",
            path,
            "/user:" + userName,
            password,
            "/persistent:no"
        };
    }

    public int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public String getHostName() {
        String name = null;

        if (path != null && path.startsWith("\\\\")) {
            int indexOf = path.indexOf("\\", 2);

            if (indexOf > 0) {
                name = path.substring(2, indexOf);
            }
        }
        return name;
    }

    public String getName() {
        String name = null;

        if (path != null) {
            int indexOf = path.lastIndexOf("\\");

            if (indexOf > 0) {
                name = path.substring(indexOf + 1);
            }
        }

        if (name == null && letter != null) {
            name = letter;
        }
        return name;
    }

    public String getFriendlyName() {
        return letter + " (" + getName() + " on " + getHostName() + ")";
    }

    @Override
    public String toString() {
        return getName();
    }

}
