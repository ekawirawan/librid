package com.uts.mobprog210040138.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class ModelLoanReq {
    @SerializedName("borrower_id")
    @Expose
    private String borrowerId;
    @SerializedName("borrowed_book_id")
    @Expose
    private String borrowedBookId;

    public ModelLoanReq (String borrowerIdP,String borrowedBookIdP) {
        this.borrowerId = borrowerIdP;
        this.borrowedBookId = borrowedBookIdP;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getBorrowedBookId() {
        return borrowedBookId;
    }

    public void setBorrowedBookId(String borrowedBookId) {
        this.borrowedBookId = borrowedBookId;
    }
}







