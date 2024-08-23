package com.MinGen.MinGen_backend.Repository;

import com.MinGen.MinGen_backend.Entity.Playlist;
import com.MinGen.MinGen_backend.Entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepo extends JpaRepository<Playlist, Long> {
    List<Playlist> findByOwner(UserDetails user);
    Playlist findPlaylistByPlaylistId(String playlistId);
    boolean existsByPlaylistId(String playlistId);
}

