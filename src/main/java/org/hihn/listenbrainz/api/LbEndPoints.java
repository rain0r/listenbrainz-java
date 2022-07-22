package org.hihn.listenbrainz.api;

import java.util.Map;
import org.hihn.listenbrainz.lb.ListenBrainzToken;
import org.hihn.listenbrainz.lb.ListenCountPayload;
import org.hihn.listenbrainz.lb.ListensRoot;
import org.hihn.listenbrainz.lb.NowPlayingRoot;
import org.hihn.listenbrainz.lb.SubmitListens;
import org.hihn.listenbrainz.lb.SubmitResponse;
import org.hihn.listenbrainz.lb.UserArtistsPayload;
import org.hihn.listenbrainz.lb.UserRecommendationRecordingsPayload;
import org.hihn.listenbrainz.lb.UserRecordingsPayload;
import org.hihn.listenbrainz.lb.UserReleases;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface LbEndPoints {

	@GET("/1/validate-token")
	Call<ListenBrainzToken> validateToken(@Header("Authorization") String token);

	@GET()
	Call<ListensRoot> getListens(@Url String url, @QueryMap Map<String, String> options);

	@POST("/1/submit-listens")
	Call<SubmitResponse> submitListens(@Header("Authorization") String token, @Body SubmitListens submitListen);

	@GET()
	Call<NowPlayingRoot> getPlayingNow(@Url String url);

	@GET()
	Call<UserArtistsPayload> getUserArtists(@Url String url, @QueryMap Map<String, String> options);

	@GET()
	Call<UserRecommendationRecordingsPayload> getUserRecommendationRecordings(@Url String url,
			@QueryMap Map<String, String> options);

	@GET()
	Call<UserRecordingsPayload> getUserRecordings(@Url String url, @QueryMap Map<String, String> options);

	@GET()
	Call<UserReleases> getUserReleases(@Url String url, @QueryMap Map<String, String> options);

	@GET()
	Call<ListenCountPayload> getUserListenCount(@Url String url);

}
