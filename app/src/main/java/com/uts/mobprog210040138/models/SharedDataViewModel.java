package com.uts.mobprog210040138.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedDataViewModel extends ViewModel {
    private String memberId;
    private String username;
    private String bookId;
    private String title;

    private String idLoan;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdLoan() {
        return idLoan;
    }

    public void setIdLoan(String idLoan) {
        this.idLoan = idLoan;
    }
}
