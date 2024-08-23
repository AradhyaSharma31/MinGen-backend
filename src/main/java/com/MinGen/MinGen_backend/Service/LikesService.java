package com.MinGen.MinGen_backend.Service;

import com.MinGen.MinGen_backend.Entity.Likes;
import com.MinGen.MinGen_backend.Entity.Playlist;
import com.MinGen.MinGen_backend.Entity.UserDetails;

import java.util.List;

public interface LikesService {
    Likes likePlaylist(UserDetails user, Playlist playlist);
    List<Likes> getAllLikesForPlaylist(Playlist playlist);
//    List<Playlist> getAllLikedPlaylistsByUser(UserDetails user);
    boolean isPlaylistLikedByUser(UserDetails user, Playlist playlist);
}
