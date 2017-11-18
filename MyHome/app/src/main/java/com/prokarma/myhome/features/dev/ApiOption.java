package com.prokarma.myhome.features.dev;

/**
 * Created by kwelsh on 11/17/17.
 */

public class ApiOption {
    private String title;
    private String description;
    private String warning;
    private String preference;

    public ApiOption(String title, String description, String warning, String preference) {
        this.title = title;
        this.description = description;
        this.warning = warning;
        this.preference = preference;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }
}
