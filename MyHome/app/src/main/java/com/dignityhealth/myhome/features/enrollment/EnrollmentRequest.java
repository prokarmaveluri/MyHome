package com.dignityhealth.myhome.features.enrollment;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by cmajji on 4/26/17.
 */

public class EnrollmentRequest implements Parcelable {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean hasAcceptedTerms = false;
    private Boolean skipVerification = false;
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

    static public class RecoveryQuestion implements Parcelable {

        private String question;
        private String answer;

        public RecoveryQuestion(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.question);
            dest.writeString(this.answer);
        }

        protected RecoveryQuestion(Parcel in) {
            this.question = in.readString();
            this.answer = in.readString();
        }

        public static final Creator<RecoveryQuestion> CREATOR = new Creator<RecoveryQuestion>() {
            @Override
            public RecoveryQuestion createFromParcel(Parcel source) {
                return new RecoveryQuestion(source);
            }

            @Override
            public RecoveryQuestion[] newArray(int size) {
                return new RecoveryQuestion[size];
            }
        };
    }


    public void setHasAcceptedTerms(Boolean hasAcceptedTerms) {
        this.hasAcceptedTerms = hasAcceptedTerms;
    }

    public void setSkipVerification(Boolean skipVerification) {
        this.skipVerification = skipVerification;
    }

    public void setRecoveryQuestions(List<RecoveryQuestion> recoveryQuestions) {
        this.recoveryQuestions = recoveryQuestions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeValue(this.hasAcceptedTerms);
        dest.writeValue(this.skipVerification);
        dest.writeTypedList(this.recoveryQuestions);
    }

    protected EnrollmentRequest(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.hasAcceptedTerms = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.skipVerification = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.recoveryQuestions = in.createTypedArrayList(RecoveryQuestion.CREATOR);
    }

    public static final Creator<EnrollmentRequest> CREATOR = new Creator<EnrollmentRequest>() {
        @Override
        public EnrollmentRequest createFromParcel(Parcel source) {
            return new EnrollmentRequest(source);
        }

        @Override
        public EnrollmentRequest[] newArray(int size) {
            return new EnrollmentRequest[size];
        }
    };
}

