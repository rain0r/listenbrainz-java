package org.hihn.listenbrainz.interceptor;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingInterceptor implements Interceptor {

	private static final Logger LOG = LogManager.getLogger(LoggingInterceptor.class);

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		long startTime = System.nanoTime();
		LOG.trace("Sending " + request.method() + "-request to: " + request.url());
		LOG.trace("Request Headers: " + request.headers());
		Response response = chain.proceed(request);
		long endTime = System.nanoTime();
		LOG.trace("" + String.format("Response %s for %s in %.1fms%n%s", response.code(), response.request().url(),
				(endTime - startTime) / 1e6d, response.headers()));
		LOG.trace("Response body: {}", response.peekBody(1048576).string());

		return response;
	}

}