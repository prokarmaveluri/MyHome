package com.prokarma.myhome.features.update;

/**
 * Created by cmajji on 6/12/17.
 */

public class UpdateResponse {

    private OS ios;
    private OS android;
    private Api api;

    public OS getIos() {
        return ios;
    }

    public void setIos(OS ios) {
        this.ios = ios;
    }

    public OS getAndroid() {
        return android;
    }

    public void setAndroid(OS android) {
        this.android = android;
    }

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }


    public class OS {

        private String current;
        private String update;
        private String forceupdate;

        public String getCurrent() {
            return current;
        }

        public void setCurrent(String current) {
            this.current = current;
        }

        public String getUpdate() {
            return update;
        }

        public void setUpdate(String update) {
            this.update = update;
        }

        public String getForceupdate() {
            return forceupdate;
        }

        public void setForceupdate(String forceupdate) {
            this.forceupdate = forceupdate;
        }

    }

    public class Api {

        private String current;
        private String lastsupported;

        public String getCurrent() {
            return current;
        }

        public void setCurrent(String current) {
            this.current = current;
        }

        public String getLastsupported() {
            return lastsupported;
        }

        public void setLastsupported(String lastsupported) {
            this.lastsupported = lastsupported;
        }

    }
}
