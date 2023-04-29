package org.hihn.listenbrainz;

import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hihn.listenbrainz.api.LbEndPoints;
import org.hihn.listenbrainz.api.QueryParameter;
import org.hihn.listenbrainz.interceptor.LoggingInterceptor;
import org.hihn.listenbrainz.interceptor.RateLimitInterceptor;
import org.hihn.listenbrainz.lb.*;
import org.hihn.listenbrainz.lb.SubmitListens.ListenType;
import org.hihn.listenbrainz.model.ArtistType;
import org.hihn.listenbrainz.model.TimeRange;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.*;

/**
 * Service that provides methods to communicate with the ListenBrainz api.
 */
public class LbService implements ListenBrainzService {

	private static final Logger LOG = LogManager.getLogger(LoggingInterceptor.class);

	public static final String ROOT_URL = "https://api.listenbrainz.org";

	public static final int DEFAULT_COUNT = 25;

	private final LbEndPoints lbEndPoints;

	private ListenBrainzToken token;

	private String authToken = "";

	public LbService() {
		this("");
	}

	public LbService(String authToken) {
		Retrofit retrofit = buildRetrofit();
		lbEndPoints = retrofit.create(LbEndPoints.class);
		if (authToken.equals("")) {
			LOG.debug("No token provided - executing only unauthenticated API calls.");
		}
		else {
			setAuthToken(authToken);
		}

	}

	@Override
	public Optional<Listens> getListens(String username) {
		try {
			Map<String, String> options = new HashMap<>();
			return Optional.ofNullable(
					lbEndPoints.getListens("/1/user/" + username + "/listens", options).execute().body().getPayload());
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return Optional.empty();
		}
	}

	@Override
	public Optional<Listens> getListens(String username, int maxTs, int minTs, int count) {
		try {
			Map<String, String> options = new HashMap<>();
			options.put(QueryParameter.MAX_TS, String.valueOf(maxTs));
			options.put(QueryParameter.MIN_TS, String.valueOf(minTs));
			options.put(QueryParameter.COUNT, String.valueOf(count));
			return Optional.ofNullable(
					lbEndPoints.getListens("/1/user/" + username + "/listens", options).execute().body().getPayload());
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return Optional.empty();
		}
	}

	@Override
	public Optional<NowPlayingTrackMetadata> getPlayingNow(String username) {
		try {
			NowPlayingRoot payload = lbEndPoints.getPlayingNow("/1/user/" + username + "/listens").execute().body();
			if (payload == null) {
				return Optional.empty();
			}
			return Optional.ofNullable(payload.getPayload().getListens().get(0).getTrackMetadata());

		}
		catch (IOException | IndexOutOfBoundsException e) {
			LOG.error(e.getMessage(), e);
			return Optional.empty();
		}
	}

	@Override
	public Optional<UserArtistsPayload> getUserArtists(String username) {
		return getUserArtists(username, DEFAULT_COUNT, 0, TimeRange.ALL_TIME);
	}

	@Override
	public Optional<UserArtistsPayload> getUserArtists(String username, int count, int offset, TimeRange timeRange) {
		try {
			Map<String, String> options = new HashMap<>();
			options.put(QueryParameter.OFFSET, String.valueOf(offset));
			options.put(QueryParameter.TIME_RANGE, timeRange.getValue());
			options.put(QueryParameter.COUNT, String.valueOf(count));
			return Optional.ofNullable(
					lbEndPoints.getUserArtists("/1/user/" + username + "/listens", options).execute().body());
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return Optional.empty();
		}
	}

	@Override
	public int getUserListenCount(String username) {
		try {
			ListenCountPayload listenCount = lbEndPoints.getUserListenCount("/1/user/" + username + "/listen-count")
					.execute().body();
			if (listenCount != null) {
				return listenCount.getCount();
			}
			return 0;
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	public Optional<UserRecommendationRecordingsPayload> getUserRecommendationRecordings(String username) {
		return getUserRecommendationRecordings(username, ArtistType.TOP, DEFAULT_COUNT, 0);
	}

	@Override
	public Optional<UserRecommendationRecordingsPayload> getUserRecommendationRecordings(String username,
			ArtistType artistType, int count, int offset) {
		try {
			Map<String, String> options = new HashMap<>();
			options.put(QueryParameter.OFFSET, String.valueOf(offset));
			options.put(QueryParameter.COUNT, String.valueOf(count));
			String url = "1/cf/recommendation/user/" + username + "/recording?artist_type=" + artistType.getValue();
			UserRecommendationRecordings recordings = lbEndPoints.getUserRecommendationRecordings(url, options)
					.execute().body();
			return Optional.ofNullable(recordings.getPayload());
		}
		catch (IOException | NullPointerException e) {
			LOG.error(e.getMessage(), e);
			return Optional.empty();
		}
	}

	@Override
	public Optional<UserRecordingsPayload> getUserRecordings(String username) {
		return getUserRecordings(username, DEFAULT_COUNT, 0, TimeRange.ALL_TIME);
	}

	@Override
	public Optional<UserRecordingsPayload> getUserRecordings(String username, int count, int offset,
			TimeRange timeRange) {
		try {
			Map<String, String> options = new HashMap<>();
			options.put(QueryParameter.OFFSET, String.valueOf(offset));
			options.put(QueryParameter.TIME_RANGE, timeRange.getValue());
			options.put(QueryParameter.COUNT, String.valueOf(count));
			UserRecordingsPayload payload = lbEndPoints
					.getUserRecordings("1/stats/user/" + username + "/recordings", options).execute().body();
			return Optional.ofNullable(payload);
		}
		catch (IOException | NullPointerException e) {
			LOG.error(e.getMessage(), e);
			return Optional.empty();
		}
	}

	@Override
	public List<UserRelease> getUserReleases(String username) {
		return getUserReleases(username, DEFAULT_COUNT, 0, TimeRange.ALL_TIME);
	}

	@Override
	public List<UserRelease> getUserReleases(String username, int count, int offset, TimeRange timeRange) {
		List<UserRelease> ret = new ArrayList<>();
		try {
			Map<String, String> options = new HashMap<>();
			options.put(QueryParameter.OFFSET, String.valueOf(offset));
			options.put(QueryParameter.TIME_RANGE, timeRange.getValue());
			options.put(QueryParameter.COUNT, String.valueOf(count));

			UserReleases releases = lbEndPoints.getUserReleases("1/stats/user/" + username + "/recordings", options)
					.execute().body();
			if (releases != null) {
				ret.addAll(releases.getPayload().getRecordings());
			}
		}
		catch (IOException | NullPointerException e) {
			LOG.error(e.getMessage(), e);
		}
		return ret;
	}

	@Override
	public boolean isTokenValid() {
		if (getAuthToken() == null || getAuthToken().trim().isEmpty()) {
			throw new IllegalStateException("No auth token set - can't post data to ListenBrainz.");
		}
		try {
			ListenBrainzToken token = lbEndPoints.validateToken(buildAuthTokenHeader()).execute().body();
			setToken(token);
			return token != null && token.isValid();
		}
		catch (IOException | NullPointerException e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public void submitMultipleListens(List<SubmitListen> listens) {
		postSubmitListens(listens, ListenType.IMPORT);
	}

	@Override
	public void submitPlayingNow(String artist, String title) {
		submitPlayingNow(artist, title, "");
	}

	@Override
	public void submitPlayingNow(String artist, String title, String album) {
		SubmitListensTrackMetadata submitListensTrackMetadata = buildSubmitListensTrackMetadata(artist, title, album);
		SubmitListenNow submitListenNow = new SubmitListenNow(submitListensTrackMetadata);
		List<SubmitListenNow> payload = List.of(submitListenNow);
		submitPlayingNow(payload);
	}

	@Override
	public void submitPlayingNow(List<SubmitListenNow> submitListenNows) {
		postSubmitListensNow(submitListenNows);
	}

	private void postSubmitListensNow(List<SubmitListenNow> submitListensNows) {
		if (getAuthToken() == null || getAuthToken().trim().isEmpty()) {
			throw new IllegalStateException("No auth token set - can't post data to ListenBrainz.");
		}
		try {
			SubmitListensNow submitListens = new SubmitListensNow(SubmitListensNow.ListenType.PLAYING_NOW,
					submitListensNows);
			SubmitResponse response = lbEndPoints.submitListenNow(buildAuthTokenHeader(), submitListens).execute()
					.body();
			LOG.trace("Scrobble response: {} ", response);
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void submitSingleListen(String artist, String title, int listenedAt) {
		submitSingleListen(artist, title, "", listenedAt);
	}

	@Override
	public void submitSingleListen(String artist, String title, String album, int listenedAt) {
		submitSingleListen(buildSubmitListen(artist, title, album, listenedAt));
	}

	@Override
	public void submitSingleListen(SubmitListen submitListen) {
		postSubmitListens(List.of(submitListen), ListenType.SINGLE);
	}

	private void postSubmitListens(List<SubmitListen> listens, SubmitListens.ListenType listenType) {
		if (getAuthToken() == null || getAuthToken().trim().isEmpty()) {
			throw new IllegalStateException("No auth token set - can't post data to ListenBrainz.");
		}
		try {
			SubmitListens submitListens = new SubmitListens(listenType, listens);
			SubmitResponse response = lbEndPoints.submitListenNow(buildAuthTokenHeader(), submitListens).execute()
					.body();
			LOG.trace("Scrobble response: {} ", response);
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private Retrofit buildRetrofit() {
		return new Retrofit.Builder().baseUrl(ROOT_URL).addConverterFactory(GsonConverterFactory.create())
				.client(new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor())
						.addInterceptor(new RateLimitInterceptor())
						.addNetworkInterceptor(chain -> chain.proceed(chain.request())).build())
				.build();
	}

	private String buildAuthTokenHeader() {
		return "Token " + getAuthToken();
	}

	private SubmitListen buildSubmitListen(String artist, String title, String album, int listenedAt) {
		SubmitListensTrackMetadata metadata = buildSubmitListensTrackMetadata(artist, title, album);
		SubmitListen submitListen = new SubmitListen();
		submitListen.setListenedAt(listenedAt);
		submitListen.setTrackMetadata(metadata);
		return submitListen;
	}

	private SubmitListensTrackMetadata buildSubmitListensTrackMetadata(String artist, String title, String album) {
		SubmitListensTrackMetadata metadata = new SubmitListensTrackMetadata();
		metadata.setTrackName(title);
		metadata.setArtistName(artist);
		metadata.setReleaseName(album);
		return metadata;
	}

	public ListenBrainzToken getToken() {
		return token;
	}

	public void setToken(ListenBrainzToken token) {
		this.token = token;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

}
