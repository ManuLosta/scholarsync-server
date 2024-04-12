package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, String> {

}
