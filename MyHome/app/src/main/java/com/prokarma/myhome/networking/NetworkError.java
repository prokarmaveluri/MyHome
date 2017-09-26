package com.prokarma.myhome.networking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kwelsh on 9/26/17.
 */

public class NetworkError {

    @SerializedName("protocol")
    @Expose
    private Boolean protocol;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("url")
    @Expose
    private String url;

    public Boolean getProtocol() {
        return protocol;
    }

    public void setProtocol(Boolean protocol) {
        this.protocol = protocol;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "NetworkError{" +
                "protocol=" + protocol +
                ", message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}