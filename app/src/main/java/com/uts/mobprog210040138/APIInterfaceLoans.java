package com.uts.mobprog210040138;


import com.uts.mobprog210040138.models.ModelAPIResLoans;
import com.uts.mobprog210040138.models.ModelAPIResSingleLoans;
import com.uts.mobprog210040138.models.ModelLoanReq;
import com.uts.mobprog210040138.models.ModelLoans;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterfaceLoans {
    String API_KEY = "b56FW7ZYRpVVF570B7nxNIWdz5xDtV3H6VUK0pRFI5wp8IGCtFWk2UNAVEXQTS9NYnLUcAV193Pia8mZ";

    //get all loans
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @GET("/loan")
    Call<ModelAPIResLoans> getAllLoan();

    //get loan by name
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @GET("/loan/search")
    Call<ModelAPIResLoans> getAllLoanByUsername(@Query("query") String query);

    //add a loan
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @POST("/loan")
    Call<ModelAPIResSingleLoans> addLoan(@Body ModelLoanReq reqBody);

    //get book by id
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @GET("/loan/{id}")
    Call<ModelAPIResSingleLoans> getLoanById(
            @Path("id") String loanId
    );

    //update loan
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @PUT("/loan/{id}")
    Call<ModelAPIResSingleLoans> updateLoan(
            @Path("id") String loanId,
            @Body ModelLoanReq reqBody
    );

    //delete loan
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @DELETE("/loan/{id}")
    Call<ModelAPIResSingleLoans> deleteLoan (
            @Path("id") String loanId
    );

    //update return status loan
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @PATCH("/loan/return-status/{id}")
    Call<ModelAPIResSingleLoans> updateReturnStatusLoan (
            @Path("id") String loanId
    );











}
