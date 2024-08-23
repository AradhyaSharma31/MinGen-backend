package com.MinGen.MinGen_backend.Controller;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.MinGen.MinGen_backend.Config.SpotifyConfig;
import com.MinGen.MinGen_backend.Entity.Playlist;
import com.MinGen.MinGen_backend.Repository.UserRepo;
import com.MinGen.MinGen_backend.Service.PlaylistService;
import com.MinGen.MinGen_backend.Service.UserService;

import jakarta.websocket.Session;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class SpotifyController {

    @Value("${custom.server.ip}")
    private String customIp;

    @Autowired
    private UserService userProfileService;

    @Autowired
    private SpotifyConfig spotifyConfiguration;

    @Autowired
    private UserRepo userDetailsRepository;

    @Autowired
    private PlaylistService playlistService;

    // Store state values in memory for simplicity (use a more robust solution for production)
    private Map<String, String> stateStore = new ConcurrentHashMap<>();

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> spotifyLogin() {
        try {
            // Generate random state string
            String state = generateRandomString(16);

            // Store state in the state store
            stateStore.put(state, "pending");

            SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();
            AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                    .scope("user-library-read user-top-read user-read-email user-read-private playlist-modify-public playlist-modify-private")
                    .state(state) // Add the state parameter to the request
                    .show_dialog(true)
                    .build();

            URI uri = authorizationCodeUriRequest.execute();
            Map<String, String> response = new HashMap<>();
            response.put("spotifyAuthUrl", uri.toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, Object>> getSpotifyUserCode(
            @RequestParam("code") String userCode,
            @RequestParam("state") String state) throws IOException, ParseException, SpotifyWebApiException {
        try {
            SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();

            // Check if the state is valid and matches the stored state (security check)
//            if (!userProfileService.isValidState(state)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("error", "Invalid state parameter"));
//            }

            // Exchange authorization code for tokens
            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(userCode).build();
            AuthorizationCodeCredentials authorizationCode = authorizationCodeRequest.execute();

            // Create a new SpotifyApi instance with the obtained tokens
            spotifyApi.setAccessToken(authorizationCode.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCode.getRefreshToken());

            // Fetch user profile
            GetCurrentUsersProfileRequest getCurrentUsersProfile = spotifyApi.getCurrentUsersProfile().build();
            User user = getCurrentUsersProfile.execute();

            // Check if the user already exists
            if (userProfileService.userExists(user.getId())) {
                // Check if the token has expired
                if (userProfileService.isTokenExpired(user.getId())) {
                    // Refresh the token using the refresh token
                    spotifyApi = refreshAccessToken(userProfileService.getRefreshToken(user.getId()), user.getId());
                }
            }

            // Store user details in the database
            userProfileService.InsertOrUpdateUserDetails(user, spotifyApi.getAccessToken(), spotifyApi.getRefreshToken());

            // Prepare response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", user.getId());
            responseData.put("username", user.getDisplayName());
            responseData.put("email", user.getEmail());
            responseData.put("accessToken", spotifyApi.getAccessToken());
            responseData.put("refreshToken", spotifyApi.getRefreshToken());
            responseData.put("country", user.getCountry());
            responseData.put("displayName", user.getDisplayName());

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error processing Spotify user code: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/createPlaylist")
    public ResponseEntity<Map<String, Object>> createPlaylist(
            @RequestParam String userId,
            @RequestParam String playlistName,
            @RequestParam String description,
            @RequestParam boolean isPublic,
            @RequestParam String accessToken
    ) {
        Playlist playlist = playlistService.createPlaylist(userId, accessToken, playlistName, description, isPublic);

        Map<String, Object> res = new HashMap<>();
        res.put("playlistId", playlist.getId());
        res.put("playlistName", playlist.getPlaylistName());
        res.put("description", playlist.getDescription());
        res.put("isPublic", playlist.isPublic());

        return ResponseEntity.ok(res);
    }

    @PostMapping("/playlists/{playlistId}/tracks")
    public ResponseEntity<String> addTracksToPlaylist(
            @PathVariable String playlistId,
            @RequestParam List<String> trackUris,
            @RequestParam String accessToken
    ) {
        try {
            playlistService.addTracksToPlaylist(playlistId, trackUris, accessToken);
            return ResponseEntity.ok("Tracks added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding tracks to playlist: " + e.getMessage());
        }
    }

    @GetMapping("/home")
    public ResponseEntity<String> home(@RequestParam String userId) {
        try {
            // Assuming home endpoint is meant to return userId for testing
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while landing on home page: " + e.getMessage());
        }
    }

    // Random string generation for state
    private String generateRandomString(int length) {
        return new Random().ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }


    public SpotifyApi refreshAccessToken(String refreshToken, String userId) throws IOException, SpotifyWebApiException, ParseException {

        SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();

        // Use the refresh token to get a new access token
        AuthorizationCodeCredentials authorizationCodeCredentials = spotifyApi.authorizationCodeRefresh()
                .refresh_token(refreshToken)
                .build()
                .execute();

        // Update the SpotifyApi instance with the new access token
        spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());

        // Optionally update the user's access token in your database
        userProfileService.updateAccessToken(userProfileService.getUserById(userId), authorizationCodeCredentials.getAccessToken());

        return spotifyApi;
    }
}

