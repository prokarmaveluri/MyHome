package com.prokarma.myhome.features.fad.details.booking.req.scheduling;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kwelsh on 6/19/17.
 */

public class Error {

    private String status;
    private String code;
    private String title;
    private String detail;
    private Source source;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }


    public class Source {

        @SerializedName("Pointer")
        private String pointer;

        public String getPointer() {
            return pointer;
        }

        public void setPointer(String pointer) {
            this.pointer = pointer;
        }
    }
}

