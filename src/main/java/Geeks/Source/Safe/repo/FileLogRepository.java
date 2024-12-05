package Geeks.Source.Safe.repo;

import Geeks.Source.Safe.Entity.FileLog;
import Geeks.Source.Safe.Entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileLogRepository extends JpaRepository<FileLog, UUID> {
    @Query(value = "SELECT fl FROM FileLog fl WHERE fl.file.id = :fileId",nativeQuery = true)
    List<FileLog> findByFileId(@Param("fileId") UUID fileId);

    @Query(value ="SELECT g FROM Group g JOIN g.files f WHERE f.id = :fileId" ,nativeQuery = true)
    Optional<Group> findGroupByFileId(@Param("fileId") UUID fileId);

}
