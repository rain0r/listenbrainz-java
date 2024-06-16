package org.hihn.listenbrainz.api.v2;

import org.hihn.listenbrainz.lb.ListenBrainzToken;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface Core {

    @GET("/1/validate-token")
    Call<ListenBrainzToken> validateToken(@Header("Authorization") String token);


}
