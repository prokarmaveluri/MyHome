package com.prokarma.myhome.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cmajji on 6/12/17.
 */

public class UpdateResponse {

    private Services services;

    public Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    public class Ciam {

        private String forceUpdate;
        private String suggestUpdate;
        private Clients clients;

        public String getForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(String forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public String getSuggestUpdate() {
            return suggestUpdate;
        }

        public void setSuggestUpdate(String suggestUpdate) {
            this.suggestUpdate = suggestUpdate;
        }

        public Clients getClients() {
            return clients;
        }

        public void setClients(Clients clients) {
            this.clients = clients;
        }

    }

    public class Clients {

        @SerializedName("myhomemobile-android")
        private MyhomeMobileAndroid myhomemobileAndroid;
        @SerializedName("myhomemobile-ios")
        private MyhomeMobileIos myhomemobileIos;

        public MyhomeMobileAndroid getMyhomemobileAndroid() {
            return myhomemobileAndroid;
        }

        public void setMyhomemobileAndroid(MyhomeMobileAndroid myhomemobileAndroid) {
            this.myhomemobileAndroid = myhomemobileAndroid;
        }

        public MyhomeMobileIos getMyhomemobileIos() {
            return myhomemobileIos;
        }

        public void setMyhomemobileIos(MyhomeMobileIos myhomemobileIos) {
            this.myhomemobileIos = myhomemobileIos;
        }

    }

    public class MyhomeMobileAndroid {

        private String forceUpdate;
        private String suggestUpdate;

        public String getForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(String forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public String getSuggestUpdate() {
            return suggestUpdate;
        }

        public void setSuggestUpdate(String suggestUpdate) {
            this.suggestUpdate = suggestUpdate;
        }

    }

    public class MyhomeMobileIos {

        private String forceUpdate;
        private String suggestUpdate;

        public String getForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(String forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public String getSuggestUpdate() {
            return suggestUpdate;
        }

        public void setSuggestUpdate(String suggestUpdate) {
            this.suggestUpdate = suggestUpdate;
        }

    }

    public class Services {

        private Ciam ciam;

        public Ciam getCiam() {
            return ciam;
        }

        public void setCiam(Ciam ciam) {
            this.ciam = ciam;
        }

    }
}
