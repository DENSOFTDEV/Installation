package com.densoftdevelopers.installation;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Spinner_name_Interface {

    String JSONURL = "http://178.128.114.85/app_files/installation/";

    @GET("fetchDetails.php")
    Call<String> getSITENAMES();

    @GET("fetch_siteId.php")
    Call<String> getSITEID();
}
