package org.hihn.listenbrainz.api;

import org.hihn.listenbrainz.lb.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

/**
 * Defines request methods for the ListenBrainz api endpoints.
 */
public interface LbEndPoints {

	@GET("/1/validate-token")
	Call<ListenBrainzToken> validateToken(@Header("Authorization") String token);

	@GET()
	Call<ListensRoot> getListens(@Url String url, @QueryMap Map<String, String> options);

	@POST("/1/submit-listens")
	Call<SubmitResponse> submitListenNow(@Header("Authorization") String token, @Body SubmitListens submitListen);

	@POST("/1/submit-listens")
	Call<SubmitResponse> submitListenNow(@Header("Authorization") String token,
			@Body SubmitListensNow submitListensNow);

	@GET()
	Call<NowPlayingRoot> getPlayingNow(@Url String url);

	@GET()
	Call<UserArtistsPayload> getUserArtists(@Url String url, @QueryMap Map<String, String> options);

	@GET()
	Call<UserRecommendationRecordings> getUserRecommendationRecordings(@Url String url,
			@QueryMap Map<String, String> options);

	@GET()
	Call<UserRecordings> getUserRecordings(@Url String url, @QueryMap Map<String, String> options);

	@GET()
	Call<UserReleases> getUserReleases(@Url String url, @QueryMap Map<String, String> options);

	@GET()
	Call<ListenCount> getUserListenCount(@Url String url);

}
