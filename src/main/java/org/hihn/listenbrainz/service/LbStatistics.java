package org.hihn.listenbrainz.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hihn.listenbrainz.interceptor.LoggingInterceptor;
import retrofit2.Retrofit;

import static org.hihn.listenbrainz.Utils.buildRetrofit;

public class LbStatistics {

    private static final Logger LOG = LogManager.getLogger(LoggingInterceptor.class);

    public LbStatistics() {
        Retrofit retrofit = buildRetrofit();
    }
}
