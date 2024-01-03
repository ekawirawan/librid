package com.uts.mobprog210040138;
import com.uts.mobprog210040138.models.ModelAPIResBook;
import com.uts.mobprog210040138.models.ModelAPIResLoans;
import com.uts.mobprog210040138.models.ModelAPIResMember;
import com.uts.mobprog210040138.models.ModelAPIResSingleBook;
import com.uts.mobprog210040138.models.ModelBook;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterfaceBook {
    String API_KEY = "b56FW7ZYRpVVF570B7nxNIWdz5xDtV3H6VUK0pRFI5wp8IGCtFWk2UNAVEXQTS9NYnLUcAV193Pia8mZ";

    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @GET("/book")
    Call<ModelAPIResBook> getBookByTitle(@Query("title") String title);

    //get all loans
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @GET("/book")
    Call<ModelAPIResBook> getAllBook();

    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @GET("/book/{id}")
    Call<ModelAPIResSingleBook> getBookById(
            @Path("id") String bookId
    );

    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @GET("/book/latest")
    Call<ModelAPIResBook> getLatestBook();
}
