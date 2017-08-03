package com.prokarma.myhome.features.preferences;

import java.util.List;

/**
 * Created by cmajji on 7/25/17.
 */

public class MySavedDoctorsResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
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


    public class User {

        private List<ProviderResponse> favoriteProviders = null;

        public List<ProviderResponse> getFavoriteProviders() {
            return favoriteProviders;
        }

        public void setFavoriteProviders(List<ProviderResponse> favoriteProviders) {
            this.favoriteProviders = favoriteProviders;
        }
    }
}