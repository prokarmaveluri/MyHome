package com.dignityhealth.myhome.features.fad.suggestions;

/**
 * Created by cmajji on 5/15/17.
 */

public class SearchSuggestionResponse {

    public String Type;
    public String Category;
    public String Title;
    public String TitleHighlight;
    public String Id;
    public Object Npi;
    public Object FirstName;
    public Object LastName;
    public Object Content;
    public String ContentUrl;

    public SearchSuggestionResponse(String title, String type) {
        Title = title;
        Type = type;
    }

    public String getType() {
        return Type;
    }

    public String getCategory() {
        return Category;
    }

    public String getTitle() {
        return Title;
    }

    public String getTitleHighlight() {
        return TitleHighlight;
    }

    public String getId() {
        return Id;
    }

    public Object getNpi() {
        return Npi;
    }

    public Object getFirstName() {
        return FirstName;
    }

    public Object getLastName() {
        return LastName;
    }

    public Object getContent() {
        return Content;
    }

    public String getContentUrl() {
        return ContentUrl;
    }
}
