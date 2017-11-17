package com.prokarma.myhome.features.fad.details.booking.req.scheduling;

import com.google.gson.annotations.SerializedName;
import com.prokarma.myhome.networking.Jsonapi;

import java.util.List;

/**
 * Created by cmajji on 6/8/17.
 */

public class CreateAppointmentResponse {

    public Value value;

    public Value getValue() {
        return value;
    }

    public class Attributes {

        @SerializedName("appointment-at")
        public String appointmentAt;
        @SerializedName("appointment-type")
        public String appointmentType;
        @SerializedName("confirmation-number")
        public String confirmationNumber;
        @SerializedName("initials")
        public String initials;

        public String getAppointmentAt() {
            return appointmentAt;
        }

        public String getAppointmentType() {
            return appointmentType;
        }

        public String getConfirmationNumber() {
            return confirmationNumber;
        }

        public String getInitials() {
            return initials;
        }
    }

    public class Data {

        public String type;
        public String id;
        public Attributes attributes;

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public Attributes getAttributes() {
            return attributes;
        }
    }

    public class Value {
        public Jsonapi jsonapi;
        public Data data;
        private List<Error> errors = null;

        public Jsonapi getJsonapi() {
            return jsonapi;
        }

        public Data getData() {
            return data;
        }

        public List<Error> getErrors() {
            return errors;
        }
    }
}