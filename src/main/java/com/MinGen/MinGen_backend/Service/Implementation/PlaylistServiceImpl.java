package com.MinGen.MinGen_backend.Service.Implementation;

import com.MinGen.MinGen_backend.Config.SpotifyConfig;
import com.MinGen.MinGen_backend.Entity.Playlist;
import com.MinGen.MinGen_backend.Entity.UserDetails;
import com.MinGen.MinGen_backend.Repository.PlaylistRepo;
import com.MinGen.MinGen_backend.Exceptions.ResourceNotFoundException;
import com.MinGen.MinGen_backend.Repository.UserRepo;
import com.MinGen.MinGen_backend.Service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;

import java.util.Date;
import java.util.List;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepo playlistRepo;
    private final UserRepo userRepo;
    private final SpotifyConfig spotifyConfig;

    @Autowired
    public PlaylistServiceImpl(PlaylistRepo playlistRepo, UserRepo userRepo, SpotifyConfig spotifyConfig) {
        this.playlistRepo = playlistRepo;
        this.userRepo = userRepo;
        this.spotifyConfig = spotifyConfig;
    }

    @Override
    public Playlist createPlaylist(String userId, String accessToken, String playlistName, String description, boolean isPublic) {
        try {
            SpotifyApi spotifyApi = spotifyConfig.getSpotifyObject();
            spotifyApi.setAccessToken(accessToken);

            CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(userId, playlistName)
                    .description(description)
                    .public_(isPublic)
                    .build();

            se.michaelthelin.spotify.model_objects.specification.Playlist spotifyPlaylist = createPlaylistRequest.execute();

            UserDetails owner = userRepo.findByRefId(userId);

            Playlist playlist = new Playlist();
            playlist.setPlaylistName(spotifyPlaylist.getName());
            playlist.setDescription(spotifyPlaylist.getDescription());
            playlist.setPublic(isPublic);
            playlist.setOwner(owner);
            playlist.setCreatedAt(new Date());
            playlist.setPlaylistId(spotifyPlaylist.getId()); // Add this line

            playlistRepo.save(playlist);

            return playlist;
        } catch (Exception e) {
            throw new RuntimeException("Error creating playlist: " + e.getMessage(), e);
        }
    }

    @Override
    public void addTracksToPlaylist(String playlistId, List<String> trackUris, String accessToken) {
        try {
            Playlist playlist = playlistRepo.findPlaylistByPlaylistId(playlistId);

            SpotifyApi spotifyApi = spotifyConfig.getSpotifyObject();
            spotifyApi.setAccessToken(accessToken);

            AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi.addItemsToPlaylist(playlist.getPlaylistId(), trackUris.toArray(new String[0]))
                    .build();

            addItemsToPlaylistRequest.execute();
        } catch (Exception e) {
            throw new RuntimeException("Error adding tracks to playlist: " + e.getMessage(), e);
        }
    }

    @Override
    public Playlist getPlaylistById(String playlistId) {
        return playlistRepo.findPlaylistByPlaylistId(playlistId);
    }

    @Override
    public List<Playlist> getAllPlaylistByUser(UserDetails user) {
        return playlistRepo.findByOwner(user);
    }

    @Override
    public Playlist updatePlaylist(String playlistId, String newPlaylistName, String description, boolean isPublic) {
        Playlist playlist = playlistRepo.findPlaylistByPlaylistId(playlistId);

        if (playlist == null) {
            throw new ResourceNotFoundException("Playlist not found with id: " + playlistId);
        }

        playlist.setPlaylistName(newPlaylistName);
        playlist.setDescription(description);
        playlist.setPublic(isPublic);

        return playlistRepo.save(playlist);
    }

    @Override
    public void deletePlaylist(String playlistId) {
        Playlist playlist = playlistRepo.findPlaylistByPlaylistId(playlistId);

        if (playlist == null) {
            throw new ResourceNotFoundException("Playlist not found with id: " + playlistId);
        }

        playlistRepo.delete(playlist);
    }

    @Override
    public boolean playlistExists(String playlistId) {
        return playlistRepo.existsByPlaylistId(playlistId);
    }
}
