package com.prokarma.myhome.features.appointments;

import java.util.List;

/**
 * Created by cmajji on 7/28/17.
 */

public class MyAppointmentsResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class User {

        private List<Appointment> appointments = null;

        public List<Appointment> getAppointments() {
            return appointments;
        }

        public void setAppointments(List<Appointment> appointments) {
            this.appointments = appointments;
        }
    }

    public class Data {

        private User user;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
}
