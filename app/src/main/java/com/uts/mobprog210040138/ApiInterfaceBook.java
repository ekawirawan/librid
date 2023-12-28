package com.uts.mobprog210040138;
import com.uts.mobprog210040138.models.ModelAPIResBook;
import com.uts.mobprog210040138.models.ModelBook;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiInterfaceBook {
    String API_KEY = "apikey b56FW7ZYRpVVF570B7nxNIWdz5xDtV3H6VUK0pRFI5wp8IGCtFWk2UNAVEXQTS9NYnLUcAV193Pia8mZ";

    //get all
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @GET("/book")
    Call<ModelAPIResBook> getAllBook();

    //get by title
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @GET("APIClientBook/book")
    Call<ModelBook> getBookByTitle(@Query("title") String title);

    //create book
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @POST("/book")
    Call<ModelAPIResBook> createBook(@Body ModelBook reqBody);

    //update book
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @PUT("/book")
    Call<ModelAPIResBook> updateBook(
            @Query("book_id") String query,
            @Body ModelBook reqBody
    );

    //delete book
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @DELETE("/book")
    Call<ModelAPIResBook> deleteBook (
            @Query("book_id") String query
    );

    //update status return book
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @PATCH("/book")
    Call<ModelAPIResBook> updateReturnStatusBook (
            @Query("book_id") String query
    );

}
