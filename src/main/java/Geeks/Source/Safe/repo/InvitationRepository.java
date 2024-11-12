package Geeks.Source.Safe.repo;

import Geeks.Source.Safe.Entity.Enum.InvitationStatus;
import Geeks.Source.Safe.Entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    List<Invitation> findByGroupId(UUID groupId);
    List<Invitation> findByInvitedUserIdAndStatus(UUID invitedUserId, InvitationStatus status);
}