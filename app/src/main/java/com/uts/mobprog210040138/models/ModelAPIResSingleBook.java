package com.uts.mobprog210040138.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class ModelAPIResSingleBook {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ModelBook data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ModelBook getData() {
        return data;
    }

    public void setData(ModelBook data) {
        this.data = data;
    }
}
