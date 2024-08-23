package com.MinGen.MinGen_backend.Service.Implementation;

import com.MinGen.MinGen_backend.Entity.UserDetails;
import com.MinGen.MinGen_backend.Repository.UserRepo;
import com.MinGen.MinGen_backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.User;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails InsertOrUpdateUserDetails(User user, String accessToken, String refreshToken) {
        UserDetails userDetails = userRepo.findByRefId(user.getId());

        if(Objects.isNull(userDetails)) {
            userDetails = new UserDetails();
        }

        userDetails.setUsername(user.getDisplayName());
        userDetails.setEmailId(user.getEmail());
        userDetails.setAccessToken(accessToken);
        userDetails.setRefreshToken(refreshToken);
        userDetails.setRefId(user.getId());
        userDetails.setCountry(String.valueOf(user.getCountry()));
        userDetails.setDisplayName(user.getDisplayName());
        return userRepo.save(userDetails);
    }

    @Override
    public String getUserById(String userId) {
        // Find the user by ID using the repository
        UserDetails userDetails = userRepo.findByRefId(userId);

        return userDetails.getRefId();
    }

    public void updateAccessToken(String userId, String newAccessToken) {
        UserDetails user = userRepo.findByRefId(userId);
        user.setAccessToken(newAccessToken);
        userRepo.save(user);
    }

    @Override
    public boolean isTokenExpired(String userId) {
        UserDetails user = userRepo.findByRefId(userId);
        return user.getTokenExpirationTime().isBefore(Instant.from(LocalTime.now()));
    }

    @Override
    public String getRefreshToken(String userId) {
        UserDetails user = userRepo.findByRefId(userId);
        return user.getRefreshToken();
    }

    @Override
    public boolean userExists(String userId) {
        return userRepo.existsByRefId(userId);
    }

}
