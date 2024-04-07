package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.FriendRequest;
import com.scholarsync.server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
    boolean existsByFromAndTo(User from, User to);
    FriendRequest findFriendRequestById(String id);
    Optional<FriendRequest> findAllByTo(User user);
}
