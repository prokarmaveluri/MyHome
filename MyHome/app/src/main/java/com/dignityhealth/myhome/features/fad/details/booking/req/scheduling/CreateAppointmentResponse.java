package com.dignityhealth.myhome.features.fad.details.booking.req.scheduling;

/**
 * Created by cmajji on 6/8/17.
 */

public class CreateAppointmentResponse {

    public Value value;

    public Value getValue() {
        return value;
    }

    public class Attributes {

        public String appointmentAt;
        public String appointmentType;
        public String confirmationNumber;
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

    public class Jsonapi {

        public String getVersion() {
            return version;
        }

        public String version;
    }

    public class Value {

        public Jsonapi getJsonapi() {
            return jsonapi;
        }

        public Data getData() {
            return data;
        }

        public Jsonapi jsonapi;
        public Data data;
    }
}