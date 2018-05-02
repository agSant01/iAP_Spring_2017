//
//  ApiService.java
//  IAP
//
//  Created by Gabriel S. Santiago on 4/27/18.
//  Copyright © 2018 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Services;

import com.affiliates.iap.iapspring2017.Extensions.DebugMethods;
import com.affiliates.iap.iapspring2017.Services.ApiInterfaces.UsersAPI;

import retrofit2.Retrofit;

// Esta es la clase que pense en que fuera el hub de todos los services
public class ApiService extends DebugMethods {
    private static String BASE_URL = "https://flex.ece.uprm.edu";

    // para crear los demas endpoints sera algo asi mismo
    // y los llamas de donde la clase que sea.
    // estaba pensando que ya el dataservice tiene la logica de parse la data y el front end solo le hace un fetch,
    // seria mas facil por cuention de tiempo llamar estos endpoints desde el dataservice y
    // asi no habria que jugar mucho con la logica de los activities
    public static UsersAPI mUserServiceInstance = (UsersAPI) createInstance(UsersAPI.class);

    // creates the instance of the service

    /**
     * Aqui seria la logica general de los interceptors y everythong
     * @param classObject
     * @return
     */
    private static Object createInstance(Class classObject) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();

        return retrofit.create(classObject);
    }
}

