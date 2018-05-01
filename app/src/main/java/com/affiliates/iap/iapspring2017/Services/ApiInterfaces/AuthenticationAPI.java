//
//  AuthenticationAPI.java
//  IAP
//
//  Created by Gabriel S. Santiago on 4/27/18.
//  Copyright © 2018 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Services.ApiInterfaces;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.POST;

public interface AuthenticationAPI {
    /**
     Log In
     **/
    @POST("auth/login")
    Call<ResponseBody> login(@Field("email") int email, @Field("password") String password);

    /**
     No se cuales son los parametros
     **/
    @POST("auth/login")
    Call<ResponseBody> getUserInfo(@Field("email") int email, @Field("password") String password);

    /**
     Check permission
     NOT FINISHED: FALTA PONER LOS FIELDS
     A esta tienes dos opciones: crear una clase de signup o pasar fields como en login()
     Si usas el FieldMap cuando creas votos o esas cosas puedes usar el metodo toMap() de todos los models
     **/
    @POST("auth/signup")
    Call<ResponseBody> signUp(@FieldMap HashMap<String, Object> data);
}
