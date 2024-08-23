package com.MinGen.MinGen_backend.Service.Implementation;

import com.MinGen.MinGen_backend.Entity.Likes;
import com.MinGen.MinGen_backend.Entity.Playlist;
import com.MinGen.MinGen_backend.Entity.UserDetails;
import com.MinGen.MinGen_backend.Repository.LikesRepo;
import com.MinGen.MinGen_backend.Service.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class LikesServiceImpl implements LikesService {

    @Autowired
    private final LikesRepo likesRepo;

    public LikesServiceImpl(LikesRepo likesRepo) {
        this.likesRepo = likesRepo;
    }

    @Override
    @Transactional
    public Likes likePlaylist(UserDetails user, Playlist playlist) {
        if (this.likesRepo.existsByUserAndPlaylist(user, playlist)) {
            likesRepo.deleteByUserAndPlaylist(user, playlist);
            return null;
        }
        Likes likes = new Likes();
        likes.setPlaylist(playlist);
        likes.setUser(user);
        likes.setCreatedAt(new Date());
        return this.likesRepo.save(likes);
    }

    @Override
    public List<Likes> getAllLikesForPlaylist(Playlist playlist) {
        return this.likesRepo.findAllByPlaylist(playlist);
    }

//    @Override
//    public List<Playlist> getAllLikedPlaylistsByUser(UserDetails user) {
//        return this.likesRepo.getLikesByUser(user);
//    }

    @Override
    public boolean isPlaylistLikedByUser(UserDetails user, Playlist playlist) {
        boolean isLiked = this.likesRepo.existsByUserAndPlaylist(user, playlist);
        return isLiked;
    }
}
