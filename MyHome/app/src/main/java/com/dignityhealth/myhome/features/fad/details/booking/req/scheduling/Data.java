package com.dignityhealth.myhome.features.fad.details.booking.req.scheduling;

/**
 * Created by kwelsh on 6/14/17.
 */

public class Data {

    private String type;
    private Attributes attributes;
    private Relationships relationships;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Relationships getRelationships() {
        return relationships;
    }

    public void setRelationships(Relationships relationships) {
        this.relationships = relationships;
    }


    public class Relationships {
        private Schedule schedule;

        public Schedule getSchedule() {
            return schedule;
        }

        public void setSchedule(Schedule schedule) {
            this.schedule = schedule;
        }
    }

    public class Schedule {
        private AppointmentDetails data;

        public AppointmentDetails getData() {
            return data;
        }

        public void setData(AppointmentDetails data) {
            this.data = data;
        }
    }

    public class AppointmentDetails {
        private String id;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }
}
