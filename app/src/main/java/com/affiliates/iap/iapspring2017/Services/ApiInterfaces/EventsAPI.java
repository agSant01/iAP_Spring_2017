//
//  EventsAPI.java
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

public interface EventsAPI {
    /**
     Get events in session
     **/
    @GET("events/session/{session_id}")
    Call<ResponseBody> getAllEvents(@Path("session_id") int id);
}
