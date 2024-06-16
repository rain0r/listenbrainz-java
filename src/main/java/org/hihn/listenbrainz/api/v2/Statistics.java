package org.hihn.listenbrainz.api.v2;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Statistics {

    // @GET("/api/vehicles/DecodeVinValues/{input}")
    @GET("/1/stats/user/{userName}/artists")
    Call<Object> getTopArtistsForUser(@Path("userName") String userName);

}
