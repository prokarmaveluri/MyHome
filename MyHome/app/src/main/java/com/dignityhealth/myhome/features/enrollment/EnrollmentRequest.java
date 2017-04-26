package com.dignityhealth.myhome.features.enrollment;

import java.util.List;

/**
 * Created by cmajji on 4/26/17.
 */

public class EnrollmentRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean hasAcceptedTerms;
    private String createdByIPAddress;
    private Boolean skipVerification;
    private List<RecoveryQuestion> recoveryQuestions = null;

    public EnrollmentRequest(String firstName, String lastName, String email, String password,
                             boolean hasAcceptedTerms, String createdByIPAddress,
                             boolean skipVerification, List<RecoveryQuestion> recoveryQuestions) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.hasAcceptedTerms = hasAcceptedTerms;
        this.createdByIPAddress = createdByIPAddress;
        this.skipVerification = skipVerification;
        this.recoveryQuestions = recoveryQuestions;
    }

    public class RecoveryQuestion {

        private String question;
        private String answer;

        public RecoveryQuestion(String question, String answer) {
            this.question = question;
            this.answer = question;
        }
    }
}

