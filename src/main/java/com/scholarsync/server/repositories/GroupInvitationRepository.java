package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.GroupInvitation;
import com.scholarsync.server.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, String> {

  Optional<GroupInvitation> findByGroupAndUser(Group group, User user);
}
