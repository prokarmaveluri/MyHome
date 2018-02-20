package com.prokarma.myhome.entities;

import java.util.ArrayList;

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

        private ArrayList<Appointment> appointments = null;

        public ArrayList<Appointment> getAppointments() {
            return appointments;
        }

        public void setAppointments(ArrayList<Appointment> appointments) {
            this.appointments = appointments;
        }

        @Override
        public String toString() {
            return "User{" +
                    "appointments=" + appointments +
                    '}';
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

        @Override
        public String toString() {
            return "Data{" +
                    "user=" + user +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MyAppointmentsResponse{" +
                "data=" + data +
                '}';
    }
}
