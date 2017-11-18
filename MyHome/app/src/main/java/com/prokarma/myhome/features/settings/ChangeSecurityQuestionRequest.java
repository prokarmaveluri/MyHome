package com.prokarma.myhome.features.settings;

/**
 * Created by cmajji on 8/24/17.
 */

public class ChangeSecurityQuestionRequest {

    private String password;
    private Question question;

    public ChangeSecurityQuestionRequest() {
    }

    public ChangeSecurityQuestionRequest(String password, Question question){
        this.password = password;
        this.question = question;
    }

    public String getPassword() {
        return password;
    }

    public Question getQuestion() {
        return question;
    }


    public static class Question {

        private String question;
        private String answer;

        public Question(String question, String answer) {
            this.answer = answer;
            this.question = question;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }
    }
}
