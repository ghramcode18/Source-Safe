package Geeks.Source.Safe.repo;

import Geeks.Source.Safe.Entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TextFileRepository extends JpaRepository<File, UUID> {

    List<File> findAllByGroupId(UUID groupId);
}
