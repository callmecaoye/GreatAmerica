package com.promeets.android.pojo;

public class StatusResponse {
    String id;
    String code;
    String description;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }
}
