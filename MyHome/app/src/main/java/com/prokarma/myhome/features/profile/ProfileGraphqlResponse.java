package com.prokarma.myhome.features.profile;

/**
 * Created by kwelsh on 8/28/17.
 */

public class ProfileGraphqlResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        private Profile user;

        public Profile getUser() {
            return user;
        }
    }
}