package com.MinGen.MinGen_backend.Repository;

import com.MinGen.MinGen_backend.Entity.Likes;
import com.MinGen.MinGen_backend.Entity.Playlist;
import com.MinGen.MinGen_backend.Entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LikesRepo extends JpaRepository<Likes, Integer> {

    boolean existsByUserAndPlaylist(UserDetails user, Playlist playlist);

    @Modifying
    @Transactional
    @Query("DELETE FROM Likes l WHERE l.user = :user AND l.playlist = :playlist")
    void deleteByUserAndPlaylist(@Param("user") UserDetails user, @Param("playlist") Playlist playlist);

    @Query("SELECT l FROM Likes l WHERE l.playlist = :playlist")
    List<Likes> findAllByPlaylist(@Param("playlist") Playlist playlist);

//    @Query("SELECT l.playlist FROM likes l WHERE l.user = :user")
//    List<Playlist> getLikesByUser(@Param("user") UserDetails user);
}
