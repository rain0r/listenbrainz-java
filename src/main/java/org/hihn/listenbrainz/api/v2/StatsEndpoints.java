package org.hihn.listenbrainz.api.v2;

import org.hihn.listenbrainz.lb.v2.TopArtistsForUser;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface StatsEndpoints {

	@GET("/1/stats/user/{userName}/artists")
	Call<TopArtistsForUser> topArtistsForUser(@Path("userName") String userName);

}
