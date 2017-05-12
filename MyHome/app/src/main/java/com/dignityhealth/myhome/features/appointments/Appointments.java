package com.dignityhealth.myhome.features.appointments;

import java.util.ArrayList;

/**
 * Created by kwelsh on 5/11/17.
 */

public class Appointments {
    public Result result;
    public ArrayList<Appointments> appointments;
    public boolean isValid;
    public ArrayList<String> errors;
    public ArrayList<String> warnings;

    public Appointments(Result result, ArrayList<Appointments> appointments, boolean isValid, ArrayList<String> errors, ArrayList<String> warnings) {
        this.result = result;
        this.appointments = appointments;
        this.isValid = isValid;
        this.errors = errors;
        this.warnings = warnings;
    }

    @Override
    public String toString() {
        return "Appointments{" +
                "result=" + result +
                ", appointments=" + appointments +
                ", isValid=" + isValid +
                ", errors=" + errors +
                ", warnings=" + warnings +
                '}';
    }
}
