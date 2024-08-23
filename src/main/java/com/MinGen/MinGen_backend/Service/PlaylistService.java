package com.MinGen.MinGen_backend.Service;

import com.MinGen.MinGen_backend.Entity.Playlist;
import com.MinGen.MinGen_backend.Entity.UserDetails;

import java.util.List;

public interface PlaylistService {
    Playlist createPlaylist(String userId, String accessToken , String playlistName, String description, boolean isPublic);
    public void addTracksToPlaylist(String playlistId, List<String> trackUris, String accessToken);
    Playlist getPlaylistById(String playlistId);
    List<Playlist> getAllPlaylistByUser(UserDetails user);
    Playlist updatePlaylist(String playlistId, String newPlaylistName, String description, boolean isPublic);
    void deletePlaylist(String playlistId);
    boolean playlistExists(String playlistId);
}
