package org.hihn.listenbrainz;

import okhttp3.OkHttpClient;
import org.hihn.listenbrainz.interceptor.LoggingInterceptor;
import org.hihn.listenbrainz.interceptor.RateLimitInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.hihn.listenbrainz.Constants.ROOT_URL;

public class Utils {

    public static Retrofit buildRetrofit() {
        return new Retrofit.Builder().baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor())
                        .addInterceptor(new RateLimitInterceptor())
                        .addNetworkInterceptor(chain -> chain.proceed(chain.request()))
                        .build())
                .build();
    }
}
