package Geeks.Source.Safe.repo;

import Geeks.Source.Safe.Entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserLogRepository extends JpaRepository<UserLog, UUID> {
    @Query(value ="SELECT ul FROM UserLog ul WHERE ul.group.id = :groupId" ,nativeQuery = true)
    List<UserLog> findByGroupId(@Param("groupId") UUID groupId);
}
