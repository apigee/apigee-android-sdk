package com.apigee.sdk.data.client.callbacks;

import com.google.api.client.auth.oauth2.TokenResponse;

/**
 * Created by ApigeeCorporation on 11/19/14.
 */
public interface OAuth2ResponseCallback extends ClientCallback<TokenResponse> {
    public void onResponse(TokenResponse response);
}
