//
//  ProjectsAPI.java
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

public interface ProjectsAPI {
    /**
     Get projects in session
     **/
    @GET("projects/session/{session_id}")
    Call<ResponseBody> getAllProjects(@Path("session_id") int id);

    /**
     Get students in project
     **/
    @GET("projects/{project_id}/students")
    Call<ResponseBody> getStudentsInProject(@Path("project_id") int id);

    /**
     Get advisors in project
     **/
    @GET("projects/{project_id}/students")
    Call<ResponseBody> getAdvisorsInProject(@Path("project_id") int id);
}
