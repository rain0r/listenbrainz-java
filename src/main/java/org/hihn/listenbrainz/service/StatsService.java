package org.hihn.listenbrainz.service;

import static org.hihn.listenbrainz.Utils.buildRetrofit;

import java.io.IOException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hihn.listenbrainz.api.v2.StatsEndpoints;
import org.hihn.listenbrainz.interceptor.LoggingInterceptor;
import org.hihn.listenbrainz.lb.v2.TopArtistsForUser;
import org.hihn.listenbrainz.lb.v2.TopArtistsForUserPayload;
import retrofit2.Retrofit;

public class StatsService {

  private static final Logger LOG = LogManager.getLogger(LoggingInterceptor.class);

  private final StatsEndpoints stats;

  public StatsService() {
    Retrofit retrofit = buildRetrofit();
    stats = retrofit.create(StatsEndpoints.class);
  }

  public Optional<TopArtistsForUserPayload> topArtistsForUser(String username) {
    try {
      TopArtistsForUser response = stats.topArtistsForUser(username).execute().body();
      if (response == null) {
        return Optional.empty();
      }
      return Optional.ofNullable(response.getPayload());
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      return Optional.empty();
    }
  }

}
