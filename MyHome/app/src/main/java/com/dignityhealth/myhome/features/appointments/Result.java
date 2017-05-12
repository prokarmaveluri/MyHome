package com.dignityhealth.myhome.features.appointments;

import com.dignityhealth.myhome.features.profile.Address;

/**
 * Created by kwelsh on 5/11/17.
 */

public class Result {
    public int appointmentId;
    public boolean isActive;
    public String username;
    public String appointmentStart;
    public String appointmentType;
    public boolean isCreatedByCaregiver;
    public String caregiverName;
    public String comments;
    public String visitReason;
    public String doctorName;
    public String facilityName;
    public String facilityPhoneNumber;
    public Address facilityAddress;

    public Result(int appointmentId, boolean isActive, String username, String appointmentStart, String appointmentType, boolean isCreatedByCaregiver, String caregiverName, String comments, String visitReason, String doctorName, String facilityName, String facilityPhoneNumber, Address facilityAddress) {
        this.appointmentId = appointmentId;
        this.isActive = isActive;
        this.username = username;
        this.appointmentStart = appointmentStart;
        this.appointmentType = appointmentType;
        this.isCreatedByCaregiver = isCreatedByCaregiver;
        this.caregiverName = caregiverName;
        this.comments = comments;
        this.visitReason = visitReason;
        this.doctorName = doctorName;
        this.facilityName = facilityName;
        this.facilityPhoneNumber = facilityPhoneNumber;
        this.facilityAddress = facilityAddress;
    }

    @Override
    public String toString() {
        return "Result{" +
                "appointmentId=" + appointmentId +
                ", isActive=" + isActive +
                ", username='" + username + '\'' +
                ", appointmentStart='" + appointmentStart + '\'' +
                ", appointmentType='" + appointmentType + '\'' +
                ", isCreatedByCaregiver=" + isCreatedByCaregiver +
                ", caregiverName='" + caregiverName + '\'' +
                ", comments='" + comments + '\'' +
                ", visitReason='" + visitReason + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", facilityPhoneNumber='" + facilityPhoneNumber + '\'' +
                ", facilityAddress=" + facilityAddress +
                '}';
    }
}
