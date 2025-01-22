package Geeks.Source.Safe.repo;

import Geeks.Source.Safe.Entity.Group;
import Geeks.Source.Safe.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

    public interface GroupRepository extends JpaRepository<Group, UUID> {
            Optional<Group> findByName(String groupName);

            @Query(value = "SELECT gu.group_id FROM group_user gu WHERE gu.user_id = :userId", nativeQuery = true)
            List<UUID> findGroupsWhereUserIsMember(UUID userId);

            @Query(value = "SELECT u.* FROM users u JOIN group_user gu ON u.id = gu.user_id WHERE gu.group_id IN :groupIds", nativeQuery = true)
            List<UUID> findUsersInGroups(List<UUID> groupIds);

    }
