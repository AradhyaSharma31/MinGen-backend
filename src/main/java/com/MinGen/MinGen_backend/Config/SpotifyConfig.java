package com.MinGen.MinGen_backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

import java.net.URI;

@Service
public class SpotifyConfig {

    @Value("${custom.server.ip}")
    private String customIp;

    public SpotifyApi getSpotifyObject() {
        URI redirectedURL = SpotifyHttpManager.makeUri("http://localhost:5173");

        return new SpotifyApi.Builder()
                .setClientId("")
                .setClientSecret("")
                .setRedirectUri(redirectedURL)
                .build();
    }
}
