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
    private Boolean skipVerification;
    private List<RecoveryQuestion> recoveryQuestions = null;

    public EnrollmentRequest(String firstName, String lastName, String email, String password,
                             boolean hasAcceptedTerms,
                             boolean skipVerification, List<RecoveryQuestion> recoveryQuestions) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.hasAcceptedTerms = hasAcceptedTerms;
        this.skipVerification = skipVerification;
        this.recoveryQuestions = recoveryQuestions;
    }

    static public class RecoveryQuestion {

        private String question;
        private String answer;

        public RecoveryQuestion(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }
}

