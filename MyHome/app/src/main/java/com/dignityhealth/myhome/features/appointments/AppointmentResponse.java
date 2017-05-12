package com.dignityhealth.myhome.features.appointments;

import java.util.ArrayList;

/**
 * Created by kwelsh on 5/12/17.
 */

public class AppointmentResponse {
    public boolean isValid;
    public ArrayList<Errors> errors;
    public ArrayList<Warnings> warnings;
    public ArrayList<Appointment> appointments;

    public AppointmentResponse(boolean isValid, ArrayList<Errors> errors, ArrayList<Warnings> warnings, ArrayList<Appointment> appointments) {
        this.isValid = isValid;
        this.errors = errors;
        this.warnings = warnings;
        this.appointments = appointments;
    }

    @Override
    public String toString() {
        return "AppointmentResponse{" +
                "isValid=" + isValid +
                ", errors=" + errors +
                ", warnings=" + warnings +
                ", appointments=" + appointments +
                '}';
    }

    class Errors {
        public String code;
        public String message;

        @Override
        public String toString() {
            return "Errors{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    class Warnings {
        public String code;
        public String message;

        @Override
        public String toString() {
            return "Warnings{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
