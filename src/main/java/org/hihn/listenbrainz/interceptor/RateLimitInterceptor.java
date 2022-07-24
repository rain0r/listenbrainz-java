package org.hihn.listenbrainz.interceptor;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RateLimitInterceptor implements Interceptor {

	private static final Logger LOG = LogManager.getLogger(RateLimitInterceptor.class);

	public static final String RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";

	public static final String RATE_LIMIT_RESET_IN = "X-RateLimit-Reset-In";

	private int lastRequestTs = 0;

	private int remainingRequests = 0;

	private int ratelimitResetIn = 0;

	@Override
	public Response intercept(Chain chain) throws IOException {
		waitUntilRateLimit();

		Request request = chain.request();
		Response response = chain.proceed(request);

		updateRateLimitVariables(response);

		return response;
	}

	private void waitUntilRateLimit() {
		if (lastRequestTs == 0) {
			return;
		}
		if (remainingRequests > 0) {
			return;
		}
		if (ratelimitResetIn > 0) {
			int resetTs = lastRequestTs + ratelimitResetIn;
			int currentTs = Math.toIntExact(System.currentTimeMillis() / 1000L);
			if (currentTs < resetTs) {
				try {
					Thread.sleep((resetTs - currentTs) * 1000L);
				}
				catch (InterruptedException e) {
					LOG.error("Failed to wait for rate limit");
				}
			}
		}
	}

	private void updateRateLimitVariables(Response response) {
		lastRequestTs = Math.toIntExact(System.currentTimeMillis() / 1000L);
		try {
			remainingRequests = Integer.parseInt(response.header(RATE_LIMIT_REMAINING));
			LOG.trace("remainingRequests: {}", remainingRequests);
		}
		catch (NumberFormatException e) {
			LOG.warn("Could not get header: {} ", RATE_LIMIT_REMAINING);
		}
		try {
			ratelimitResetIn = Integer.parseInt(response.header(RATE_LIMIT_RESET_IN));
			LOG.trace("ratelimitResetIn: {}", ratelimitResetIn);
		}
		catch (NumberFormatException e) {
			LOG.warn("Could not get header: {} ", RATE_LIMIT_RESET_IN);
		}
	}

}
