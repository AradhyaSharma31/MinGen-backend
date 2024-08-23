package com.MinGen.MinGen_backend.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Table(name = "user")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails implements Serializable {
    private static final long serialVersionUID = 454786854544665654L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "country")
    private String country;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "access_token", length = 2000)
    private String accessToken;

    @Column(name = "refresh_token", length = 2000)
    private String refreshToken;

    @Column(name = "ref_id")
    private String refId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    private Date createdOn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    private Date updatedOn;

    @Column(name = "token_expiration")
    private Instant tokenExpirationTime;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Playlist> playlists;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likes;
}
