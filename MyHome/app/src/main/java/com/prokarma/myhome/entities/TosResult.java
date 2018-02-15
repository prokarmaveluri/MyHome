package com.prokarma.myhome.entities;

/**
 * Created by kwelsh on 5/10/17.
 */

public class TosResult {
    public boolean isTermsAccepted;
    public String termsAcceptedDate;

    public TosResult(boolean isTermsAccepted, String termsAcceptedDate) {
        this.isTermsAccepted = isTermsAccepted;
        this.termsAcceptedDate = termsAcceptedDate;
    }

    @Override
    public String toString() {
        return "TosResult{" +
                "isTermsAccepted=" + isTermsAccepted +
                ", termsAcceptedDate='" + termsAcceptedDate + '\'' +
                '}';
    }
}
