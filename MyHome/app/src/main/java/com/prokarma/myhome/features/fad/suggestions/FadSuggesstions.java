package com.prokarma.myhome.features.fad.suggestions;

/**
 * Created by cmajji on 7/3/17.
 */

public class FadSuggesstions {

    private String type;
    private String title;
    private String Id;

    public FadSuggesstions(String type, String title, String id) {
        this.type = type;
        this.title = title;
        this.Id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return Id;
    }
}
