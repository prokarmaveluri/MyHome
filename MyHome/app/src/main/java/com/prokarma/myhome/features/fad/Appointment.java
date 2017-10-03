package com.prokarma.myhome.features.fad;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by kwelsh on 5/18/17.
 */

public class Appointment implements Parcelable, Comparable {
    public static final String TYPE_EXISTING = "established-patient";
    public static final String TYPE_NEW = "new-patient";    //TODO Kevin - is this correct? Dr.Dong of Redding doesn't seem to have any new patient type appointments...

    public String Time;
    public ArrayList<AppointmentType> AppointmentTypes;
    public boolean Selected;
    public String FacilityId;
    public String FacilityAddress;
    public String FacilityCity;
    public String FacilityState;
    public String FacilityZip;
    public String FacilityLat;
    public String FacilityLong;
    public String RegistrationUrl;
    public String ScheduleId;
    public String FullAddress;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Time);
        dest.writeTypedList(this.AppointmentTypes);
        dest.writeByte(this.Selected ? (byte) 1 : (byte) 0);
        dest.writeString(this.FacilityId);
        dest.writeString(this.FacilityAddress);
        dest.writeString(this.FacilityCity);
        dest.writeString(this.FacilityState);
        dest.writeString(this.FacilityZip);
        dest.writeString(this.FacilityLat);
        dest.writeString(this.FacilityLong);
        dest.writeString(this.RegistrationUrl);
        dest.writeString(this.ScheduleId);
        dest.writeString(this.FullAddress);
    }

    public Appointment() {
    }

    public Appointment(String time, ArrayList<AppointmentType> appointmentTypes, boolean selected, String facilityId, String facilityAddress, String facilityCity, String facilityState, String facilityZip, String facilityLat, String facilityLong, String registrationUrl, String scheduleId, String fullAddress) {
        Time = time;
        AppointmentTypes = appointmentTypes;
        Selected = selected;
        FacilityId = facilityId;
        FacilityAddress = facilityAddress;
        FacilityCity = facilityCity;
        FacilityState = facilityState;
        FacilityZip = facilityZip;
        FacilityLat = facilityLat;
        FacilityLong = facilityLong;
        RegistrationUrl = registrationUrl;
        ScheduleId = scheduleId;
        FullAddress = fullAddress;
    }

    protected Appointment(Parcel in) {
        this.Time = in.readString();
        this.AppointmentTypes = in.createTypedArrayList(AppointmentType.CREATOR);
        this.Selected = in.readByte() != 0;
        this.FacilityId = in.readString();
        this.FacilityAddress = in.readString();
        this.FacilityCity = in.readString();
        this.FacilityState = in.readString();
        this.FacilityZip = in.readString();
        this.FacilityLat = in.readString();
        this.FacilityLong = in.readString();
        this.RegistrationUrl = in.readString();
        this.ScheduleId = in.readString();
        this.FullAddress = in.readString();
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel source) {
            return new Appointment(source);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    @Override
    public int compareTo(@NonNull Object anotherAppointment) {
        String compareDate = ((Appointment) anotherAppointment).Time;
        return this.Time.compareTo(compareDate);
    }
}
