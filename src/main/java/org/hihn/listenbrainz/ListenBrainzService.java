package org.hihn.listenbrainz;

import org.hihn.listenbrainz.lb.*;
import org.hihn.listenbrainz.model.ArtistType;
import org.hihn.listenbrainz.model.TimeRange;

import java.util.List;
import java.util.Optional;

/**
 * Interface that defines all methods needed to use the ListenBrainz api.
 */
public interface ListenBrainzService {

	// Get Listens

	/**
	 * Get {@link Listens} for user <code>username</code>.
	 * @param username ListenBrainz username for the user to query.
	 * @return An Optional, that contains the {@link Listens} data of the user.
	 */
	Optional<Listens> getListens(String username);

	/**
	 * Get listens for user <code>username</code>.
	 * <p>
	 * If none of the optional arguments are given, this function will return the 25 most
	 * recent listens. The optional <code>maxTs</code> and <code>minTs</code> UNIX epoch
	 * timestamps control at which point in time to start returning listens. You may
	 * specify <code>maxTs</code> or <code>minTs</code>, but not both in one call.
	 * @param username The username of the user whose data is to be fetched.
	 * @param maxTs If you specify a maxTs timestamp, listens with listenedAt less than
	 * (but not including) this value will be returned.
	 * @param minTs If you specify a minTs timestamp, listens with listenedAt greater than
	 * (but not including) this value will be returned.
	 * @param count The number of listens to return. Defaults to 25, maximum is 100.
	 * @return A list of listens for the user <code>username</code>.
	 */
	Optional<Listens> getListens(String username, int maxTs, int minTs, int count);

	/**
	 * Get the listen being played right now for user <code>username</code>.
	 * @param username The username of the user whose data is to be fetched.
	 * @return A single {@link NowPlayingTrackMetadata} if the user is playing something
	 * currently, else an empty Optional.
	 */
	Optional<NowPlayingTrackMetadata> getPlayingNow(String username);

	// Token

	/**
	 * Check if the specified ListenBrainz auth token is valid using the
	 * <code>/1/validate-token</code> endpoint.
	 * @return True, if it is valid, false, if it is not valid.
	 */
	boolean isTokenValid();

	// Submit Listens

	/**
	 * Submit single listen.
	 * <p>
	 * Indicates user just finished listening to track.
	 * @param artist Artist name.
	 * @param title Track title.
	 * @param listenedAt Unix time when the track was listened to.
	 */
	void submitSingleListen(String artist, String title, int listenedAt);

	/**
	 * Submit single listen.
	 * <p>
	 * Indicates user just finished listening to track.
	 * @param artist Artist name.
	 * @param title Track title.
	 * @param album Album name.
	 * @param listenedAt Unix time when the track was listened to.
	 */
	void submitSingleListen(String artist, String title, String album, int listenedAt);

	/**
	 * Submit single listen.
	 * <p>
	 * Indicates user just finished listening to track.
	 * @param submitListen {@link SubmitListen} to scrobble.
	 */
	void submitSingleListen(SubmitListen submitListen);

	/**
	 * Submit a list of listens to ListenBrainz.
	 * <p>
	 * Requires that the auth token for the user whose listens are being submitted has
	 * been set.
	 * @param listens The list of listens to be submitted.
	 */
	void submitMultipleListens(List<SubmitListen> listens);

	/**
	 * Submit a playing now notification to ListenBrainz.
	 * <p>
	 * Requires that the auth token for the user whose data is being submitted has been
	 * set.
	 * @param artist Artist name.
	 * @param title Track title.
	 */
	void submitPlayingNow(String artist, String title);

	/**
	 * Submit a playing now notification to ListenBrainz.
	 * <p>
	 * Requires that the auth token for the user whose data is being submitted has been
	 * set.
	 * @param artist Artist name.
	 * @param title Track title.
	 * @param album Album name.
	 */
	void submitPlayingNow(String artist, String title, String album);

	/**
	 * Submit a playing now notification to ListenBrainz.
	 * <p>
	 * Requires that the auth token for the user whose data is being submitted has been
	 * set.
	 * @param listen The {@link SubmitListen} to be submitted, the listen should NOT have
	 * a <code>listenedAt</code> attribute
	 */
	void submitPlayingNow(SubmitListen listen);

	// User info

	/**
	 * Get artists for user <code>username</code>, sorted in descending order of listen
	 * count.
	 * @param username The username of the user whose artists are to be fetched.
	 * @return The artists listened to by the user in the time range with listen counts
	 * and other data in the same format as the API response.
	 */
	Optional<UserArtistsPayload> getUserArtists(String username);

	/**
	 * Get artists for user <code>username</code>, sorted in descending order of listen
	 * count.
	 * @param username The username of the user whose artists are to be fetched.
	 * @param count The number of artists to fetch, defaults to 25, maximum is 100
	 * @param offset The number of artists to skip from the beginning, for pagination,
	 * defaults to 0.
	 * @param timeRange the {@link TimeRange}
	 * @return The artists listened to by the user in the time range with listen counts
	 * and other data in the same format as the API response
	 */
	Optional<UserArtistsPayload> getUserArtists(String username, int count, int offset, TimeRange timeRange);

	/**
	 * Get total number of listens for user.
	 * @param username The username of the user whose listens are to be fetched
	 * @return Number of listens returned by the Listenbrainz API.
	 */
	int getUserListenCount(String username);

	/**
	 * Get recommended recordings for a user.
	 * @param username The username of the user whose recommended tracks are to be
	 * fetched.
	 * @return The recommended recordings as other data returned by the API
	 */
	Optional<UserRecommendationRecordingsPayload> getUserRecommendationRecordings(String username);

	/**
	 * Get recommended recordings for a user.
	 * @param username The username of the user whose recommended tracks are to be
	 * fetched.
	 * @param artistType The type of filtering applied to the recommended tracks.
	 * @param count The number of recordings to fetch, defaults to 25, maximum is 100.
	 * @param offset The number of releases to skip from the beginning, for pagination,
	 * defaults to 0.
	 * @return The recommended recordings as other data returned by the API
	 */
	Optional<UserRecommendationRecordingsPayload> getUserRecommendationRecordings(String username,
			ArtistType artistType, int count, int offset);

	/**
	 * Get recordings for user <code>username</code>, sorted in descending order of listen
	 * count.
	 * @param username The username of the user whose artists are to be fetched.
	 * @return The recordings listened to by the user in the time range with listen counts
	 * and other data, in the same format as the API response
	 */
	Optional<UserRecordingsPayload> getUserRecordings(String username);

	/**
	 * Get recordings for user <code>username</code>, sorted in descending order of listen
	 * count.
	 * @param username The username of the user whose artists are to be fetched.
	 * @param count The number of recordings to fetch, defaults to 25, maximum is 100.
	 * @param offset The number of recordings to skip from the beginning, for pagination,
	 * defaults to 0.
	 * @param timeRange the {@link TimeRange}
	 * @return The recordings listened to by the user in the time range with listen counts
	 * and other data, in the same format as the API response
	 */
	Optional<UserRecordingsPayload> getUserRecordings(String username, int count, int offset, TimeRange timeRange);

	/**
	 * Get releases for user <code>username</code>, sorted in descending order of listen
	 * count.
	 * @param username The username of the user whose releases are to be fetched.
	 * @return The releases listened to by the user in the time range with listen counts
	 * and other data
	 */
	List<UserRelease> getUserReleases(String username);

	/**
	 * Get releases for user <code>username</code>, sorted in descending order of listen
	 * count.
	 * @param username The username of the user whose releases are to be fetched.
	 * @param count The number of releases to fetch, defaults to 25, maximum is 100.
	 * @param offset The number of releases to skip from the beginning, for pagination,
	 * defaults to 0.
	 * @param timeRange the {@link TimeRange}
	 * @return The releases listened to by the user in the time range with listen counts
	 * and other data
	 */
	List<UserRelease> getUserReleases(String username, int count, int offset, TimeRange timeRange);

}
