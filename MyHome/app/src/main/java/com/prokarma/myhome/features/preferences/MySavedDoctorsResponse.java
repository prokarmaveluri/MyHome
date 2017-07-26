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

    public class FavoriteProvider {

        private String firstName;
        private String lastName;
        private String npi;
        private String displayName;
        private String displayLastName;
        private String displayLastNamePlural;
        private String middleName;
        private Object suffix;
        private String title;
        private Object philosophy;
        private Boolean supportsOnlineBooking;
        private String providerDetailsUrl;
        private List<String> primarySpecialities = null;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getNpi() {
            return npi;
        }

        public void setNpi(String npi) {
            this.npi = npi;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayLastName() {
            return displayLastName;
        }

        public void setDisplayLastName(String displayLastName) {
            this.displayLastName = displayLastName;
        }

        public String getDisplayLastNamePlural() {
            return displayLastNamePlural;
        }

        public void setDisplayLastNamePlural(String displayLastNamePlural) {
            this.displayLastNamePlural = displayLastNamePlural;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public Object getSuffix() {
            return suffix;
        }

        public void setSuffix(Object suffix) {
            this.suffix = suffix;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Object getPhilosophy() {
            return philosophy;
        }

        public void setPhilosophy(Object philosophy) {
            this.philosophy = philosophy;
        }

        public Boolean getSupportsOnlineBooking() {
            return supportsOnlineBooking;
        }

        public void setSupportsOnlineBooking(Boolean supportsOnlineBooking) {
            this.supportsOnlineBooking = supportsOnlineBooking;
        }

        public String getProviderDetailsUrl() {
            return providerDetailsUrl;
        }

        public void setProviderDetailsUrl(String providerDetailsUrl) {
            this.providerDetailsUrl = providerDetailsUrl;
        }

        public List<String> getPrimarySpecialities() {
            return primarySpecialities;
        }

        public void setPrimarySpecialities(List<String> primarySpecialities) {
            this.primarySpecialities = primarySpecialities;
        }

    }

    public class User {

        private List<FavoriteProvider> favoriteProviders = null;

        public List<FavoriteProvider> getFavoriteProviders() {
            return favoriteProviders;
        }

        public void setFavoriteProviders(List<FavoriteProvider> favoriteProviders) {
            this.favoriteProviders = favoriteProviders;
        }
    }
}