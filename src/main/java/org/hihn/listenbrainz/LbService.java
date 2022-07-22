package org.hihn.listenbrainz;

import static org.hihn.listenbrainz.api.QueryParameter.COUNT;
import static org.hihn.listenbrainz.api.QueryParameter.MAX_TS;
import static org.hihn.listenbrainz.api.QueryParameter.MIN_TS;
import static org.hihn.listenbrainz.api.QueryParameter.OFFSET;
import static org.hihn.listenbrainz.api.QueryParameter.TIME_RANGE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hihn.listenbrainz.api.LbEndPoints;
import org.hihn.listenbrainz.interceptor.LoggingInterceptor;
import org.hihn.listenbrainz.lb.ListenBrainzToken;
import org.hihn.listenbrainz.lb.ListenCountPayload;
import org.hihn.listenbrainz.lb.ListensRoot;
import org.hihn.listenbrainz.lb.NowPlayingRoot;
import org.hihn.listenbrainz.lb.NowPlayingTrackMetadata;
import org.hihn.listenbrainz.lb.SubmitListen;
import org.hihn.listenbrainz.lb.SubmitListens;
import org.hihn.listenbrainz.lb.SubmitListensTrackMetadata;
import org.hihn.listenbrainz.lb.SubmitResponse;
import org.hihn.listenbrainz.lb.UserArtistsPayload;
import org.hihn.listenbrainz.lb.UserRecommendationRecordingsPayload;
import org.hihn.listenbrainz.lb.UserRecordingsPayload;
import org.hihn.listenbrainz.lb.UserRelease;
import org.hihn.listenbrainz.lb.UserReleases;
import org.hihn.listenbrainz.model.ArtistType;
import org.hihn.listenbrainz.model.TimeRange;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
	public Optional<ListensRoot> getListens(String username) {
		return getListens(username, Optional.empty(), Optional.empty(), DEFAULT_COUNT);
	}

	@Override
	public Optional<ListensRoot> getListens(String username, Optional<Integer> maxTs, Optional<Integer> minTs,
			int count) {
		try {
			Map<String, String> options = new HashMap<>();
			maxTs.ifPresent(ts -> options.put(MAX_TS, String.valueOf(ts)));
			minTs.ifPresent(ts -> options.put(MIN_TS, String.valueOf(ts)));
			options.put("count", String.valueOf(count));
			return Optional
					.ofNullable(lbEndPoints.getListens("/1/user/" + username + "/listens", options).execute().body());
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
			options.put(OFFSET, String.valueOf(offset));
			options.put(TIME_RANGE, timeRange.getValue());
			options.put(COUNT, String.valueOf(count));
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
			options.put(OFFSET, String.valueOf(offset));
			options.put(COUNT, String.valueOf(count));
			String url = "1/cf/recommendation/user/" + username + "/recording?artist_type=" + artistType.getValue();
			UserRecommendationRecordingsPayload payload = lbEndPoints.getUserRecommendationRecordings(url, options)
					.execute().body();
			return Optional.ofNullable(payload);
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
			options.put(OFFSET, String.valueOf(offset));
			options.put(TIME_RANGE, timeRange.getValue());
			options.put(COUNT, String.valueOf(count));
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
			options.put(OFFSET, String.valueOf(offset));
			options.put(TIME_RANGE, timeRange.getValue());
			options.put(COUNT, String.valueOf(count));

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
		listens.forEach(this::submitSingleListen);
	}

	@Override
	public void submitPlayingNow(String artist, String title) {
		submitPlayingNow(artist, title, "");
	}

	@Override
	public void submitPlayingNow(String artist, String title, String album) {
		int unixTime = Math.toIntExact(System.currentTimeMillis() / 1000L);
		submitPlayingNow(buildSubmitListen(artist, title, album, unixTime));
	}

	@Override
	public void submitPlayingNow(SubmitListen submitListen) {
		try {
			SubmitListens submitListens = new SubmitListens();
			submitListens.setListenType(SubmitListens.ListenType.PLAYING_NOW);
			submitListens.setPayload(List.of(submitListen));

			SubmitResponse response = lbEndPoints.submitListens(buildAuthTokenHeader(), submitListens).execute().body();
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
		try {
			SubmitListens submitListens = new SubmitListens();
			submitListens.setListenType(SubmitListens.ListenType.SINGLE);
			submitListens.setPayload(List.of(submitListen));

			SubmitResponse response = lbEndPoints.submitListens(buildAuthTokenHeader(), submitListens).execute().body();
			LOG.trace("Scrobble response: {} ", response);
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private Retrofit buildRetrofit() {
		return new Retrofit.Builder().baseUrl(ROOT_URL).addConverterFactory(GsonConverterFactory.create())
				.client(new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor())
						.addNetworkInterceptor(chain -> chain.proceed(chain.request())).build())
				.build();
	}

	private String buildAuthTokenHeader() {
		return "Token " + getAuthToken();
	}

	private SubmitListen buildSubmitListen(String artist, String title, String album, int listenedAt) {
		SubmitListensTrackMetadata metadata = new SubmitListensTrackMetadata();
		metadata.setTrackName(title);
		metadata.setArtistName(artist);
		metadata.setReleaseName(album);
		SubmitListen submitListen = new SubmitListen();
		submitListen.setListenedAt(listenedAt);
		submitListen.setTrackMetadata(metadata);
		return submitListen;
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
