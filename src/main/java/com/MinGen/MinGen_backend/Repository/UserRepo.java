package com.MinGen.MinGen_backend.Repository;

import com.MinGen.MinGen_backend.Entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserDetails, Integer> {

    UserDetails findByRefId(String refId);

    boolean existsByRefId(String userId);
}
