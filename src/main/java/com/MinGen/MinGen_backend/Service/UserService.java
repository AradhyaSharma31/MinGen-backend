package com.MinGen.MinGen_backend.Service;

import com.MinGen.MinGen_backend.Entity.UserDetails;
import se.michaelthelin.spotify.model_objects.specification.User;

public interface UserService {
    public UserDetails InsertOrUpdateUserDetails(User user, String accessToken, String refreshToken);
    public void updateAccessToken(String userId, String newAccessToken);
    public String getRefreshToken(String userId);
    public boolean isTokenExpired(String userId);
    public boolean userExists(String userId);
    public String getUserById(String userId);
}
