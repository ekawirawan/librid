package com.uts.mobprog210040138.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ModelAPIResSingleLoans {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ModelLoans data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ModelLoans getData() {
        return data;
    }

    public void setData(ModelLoans data) {
        this.data = data;
    }

}
