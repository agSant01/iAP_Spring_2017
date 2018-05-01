//
//  UsersAPI.java
//  IAP
//
//  Created by Gabriel S. Santiago on 4/27/18.
//  Copyright © 2018 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Services.ApiInterfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UsersAPI {
    /**
     Get professors list
     **/
    @GET("professors/all")
    Call<ResponseBody> getAllProfessors();

    /**
     Get professor by ID
     **/
    @GET("professors/{id}")
    Call<ResponseBody> getAllProfessors(@Path("id") int id);

    /**
     Get students list
     **/
    @GET("students/all")
    Call<ResponseBody> getAllStudents();

    /**
     Get permissions list
     **/
    @GET("permissions/all")
    Call<ResponseBody> getAllPermissions();
}
