package org.hihn.listenbrainz.api.v2;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface StatsEndpoints {

    @GET("/1/stats/user/{userName}/artists")
    Call<Object> topArtistsForUser(@Path("userName") String userName);

}
