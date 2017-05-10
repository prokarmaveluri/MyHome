package com.dignityhealth.myhome.features.tos;

/**
 * Created by kwelsh on 5/10/17.
 */

public class Result {
    public boolean isTermsAccepted;
    public String termsAcceptedDate;

    public Result(boolean isTermsAccepted, String termsAcceptedDate) {
        this.isTermsAccepted = isTermsAccepted;
        this.termsAcceptedDate = termsAcceptedDate;
    }

    @Override
    public String toString() {
        return "Result{" +
                "isTermsAccepted=" + isTermsAccepted +
                ", termsAcceptedDate='" + termsAcceptedDate + '\'' +
                '}';
    }
}
