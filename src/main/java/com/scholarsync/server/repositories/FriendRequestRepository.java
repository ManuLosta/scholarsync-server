package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.FriendRequest;
import com.scholarsync.server.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
  boolean existsByFromAndTo(User from, User to);

  Optional<FriendRequest> findAllByTo(User user);
}
