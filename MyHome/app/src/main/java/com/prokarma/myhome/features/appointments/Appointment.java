package com.prokarma.myhome.features.appointments;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.prokarma.myhome.features.profile.Address;

/**
 * Created by kwelsh on 5/11/17.
 */

public class Appointment implements Parcelable, Comparable {
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

    public Appointment(int appointmentId, boolean isActive, String username, String appointmentStart, String appointmentType, boolean isCreatedByCaregiver, String caregiverName, String comments, String visitReason, String doctorName, String facilityName, String facilityPhoneNumber, Address facilityAddress) {
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
        return "AppointmentResponse{" +
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

    @Override
    public int compareTo(@NonNull Object anotherAppointment) {
        String compareDate = ((Appointment)anotherAppointment).appointmentStart;
        return this.appointmentStart.compareTo(compareDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.appointmentId);
        dest.writeByte(this.isActive ? (byte) 1 : (byte) 0);
        dest.writeString(this.username);
        dest.writeString(this.appointmentStart);
        dest.writeString(this.appointmentType);
        dest.writeByte(this.isCreatedByCaregiver ? (byte) 1 : (byte) 0);
        dest.writeString(this.caregiverName);
        dest.writeString(this.comments);
        dest.writeString(this.visitReason);
        dest.writeString(this.doctorName);
        dest.writeString(this.facilityName);
        dest.writeString(this.facilityPhoneNumber);
        dest.writeParcelable(this.facilityAddress, flags);
    }

    public Appointment(Parcel in) {
        this.appointmentId = in.readInt();
        this.isActive = in.readByte() != 0;
        this.username = in.readString();
        this.appointmentStart = in.readString();
        this.appointmentType = in.readString();
        this.isCreatedByCaregiver = in.readByte() != 0;
        this.caregiverName = in.readString();
        this.comments = in.readString();
        this.visitReason = in.readString();
        this.doctorName = in.readString();
        this.facilityName = in.readString();
        this.facilityPhoneNumber = in.readString();
        this.facilityAddress = in.readParcelable(Address.class.getClassLoader());
    }

    public static final Parcelable.Creator<Appointment> CREATOR = new Parcelable.Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel source) {
            return new Appointment(source);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };
}
