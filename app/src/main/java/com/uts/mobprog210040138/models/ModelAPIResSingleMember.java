package com.uts.mobprog210040138.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelAPIResSingleMember {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ModelMember data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ModelMember getData() {
        return data;
    }

    public void setData(ModelMember data) {
        this.data = data;
    }
}
