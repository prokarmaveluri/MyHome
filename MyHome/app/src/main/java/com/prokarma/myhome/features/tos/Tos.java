package com.prokarma.myhome.features.tos;

import java.util.ArrayList;

/**
 * Created by kwelsh on 5/10/17.
 */

public class Tos {
    public Result result;
    public boolean isValid;
    public ArrayList<String> errors;
    public ArrayList<String> warnings;

    public Tos(Result result, boolean isValid, ArrayList<String> errors, ArrayList<String> warnings) {
        this.result = result;
        this.isValid = isValid;
        this.errors = errors;
        this.warnings = warnings;
    }

    @Override
    public String toString() {
        return "Tos{" +
                "result=" + result +
                ", isValid=" + isValid +
                ", errors=" + errors +
                ", warnings=" + warnings +
                '}';
    }
}
