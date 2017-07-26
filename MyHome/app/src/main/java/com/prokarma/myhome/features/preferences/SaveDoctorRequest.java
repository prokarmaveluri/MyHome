package com.prokarma.myhome.features.preferences;

/**
 * Created by cmajji on 7/24/17.
 */

public class SaveDoctorRequest {
    private String npi;

    public SaveDoctorRequest(String npi) {
        this.npi = npi;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }
}
