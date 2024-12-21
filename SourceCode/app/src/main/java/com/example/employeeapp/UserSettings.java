package com.example.employeeapp;

public class UserSettings {
    private boolean ptoNotifications;
    private boolean detailsNotifications;
    private boolean darkTheme;
    private boolean redGreenTheme;

    public UserSettings(boolean ptoNotifications, boolean detailsNotifications, boolean darkTheme, boolean redGreenTheme) {
        this.ptoNotifications = ptoNotifications;
        this.detailsNotifications = detailsNotifications;
        this.darkTheme = darkTheme;
        this.redGreenTheme = redGreenTheme;
    }

    public boolean getPtoNotifications() {
        return this.ptoNotifications;
    }

    public boolean getDetailsNotifications() {
        return this.detailsNotifications;
    }

    public boolean getDarkTheme() {
        return this.darkTheme;
    }

    public boolean getRedGreenTheme() {
        return this.redGreenTheme;
    }

    public void setPtoNotifications(boolean ptoNotifications) {
        this.ptoNotifications = ptoNotifications;
    }

    public void setDetailsNotifications(boolean detailsNotifications) {
        this.detailsNotifications = detailsNotifications;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }

    public void setRedGreenTheme(boolean redGreenTheme) {
        this.redGreenTheme = redGreenTheme;
    }
}
