package com.dignityhealth.myhome.features.appointments;

import java.util.ArrayList;

/**
 * Created by kwelsh on 5/12/17.
 */

public class AppointmentResponse {
    public Result result;
    public boolean isValid;
    public ArrayList<Errors> errors;
    public ArrayList<Warnings> warnings;

    public AppointmentResponse(Result result, boolean isValid, ArrayList<Errors> errors, ArrayList<Warnings> warnings) {
        this.result = result;
        this.isValid = isValid;
        this.errors = errors;
        this.warnings = warnings;
    }

    @Override
    public String toString() {
        return "AppointmentResponse{" +
                "result=" + result +
                ", isValid=" + isValid +
                ", errors=" + errors +
                ", warnings=" + warnings +
                '}';
    }

    class Result {
        public ArrayList<Appointment> appointments;

        @Override
        public String toString() {
            return "Result{" +
                    "appointments=" + appointments +
                    '}';
        }
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
