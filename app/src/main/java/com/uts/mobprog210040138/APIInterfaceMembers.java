package com.uts.mobprog210040138;

import com.uts.mobprog210040138.models.ModelAPIResMember;
import com.uts.mobprog210040138.models.ModelAPIResSingleLoans;
import com.uts.mobprog210040138.models.ModelAPIResSingleMember;
import com.uts.mobprog210040138.models.ModelLoans;
import  com.uts.mobprog210040138.models.ModelMember;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface APIInterfaceMembers {
    String API_KEY = "b56FW7ZYRpVVF570B7nxNIWdz5xDtV3H6VUK0pRFI5wp8IGCtFWk2UNAVEXQTS9NYnLUcAV193Pia8mZ";

    //get all member
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @GET("/member")
    Call<ModelAPIResMember>getAllMember();

    //get member by username
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @GET("/member/search")
    Call<ModelAPIResMember> getAllMemberByUsername(@Query("username") String query);

    //create a member
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @POST("/member")
    Call<ModelAPIResMember> createMember(@Body ModelMember reqBody);

    //update a member
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @PUT("/member")
    Call<ModelAPIResMember> updateMember(
            @Query("member_id") String query,
            @Body ModelMember reqBody);

    //delete member by name
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @DELETE("/member")
    Call<ModelAPIResMember> deleteMember(@Query("username") String query);

    //get member by id
    @Headers({"Content-Type: application/json",
            "X-API-Key: " + API_KEY
    })
    @GET("/member/{id}")
    Call<ModelAPIResSingleMember> getMemberById(
            @Path("id") String memberId
    );

}

