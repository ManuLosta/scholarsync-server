package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.GroupInvitation;
import com.scholarsync.server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, String> {

    Optional<GroupInvitation> findByGroupAndUserId(Group group, User user);
}
