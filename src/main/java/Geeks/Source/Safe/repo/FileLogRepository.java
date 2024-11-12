package Geeks.Source.Safe.repo;

import Geeks.Source.Safe.Entity.FileLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FileLogRepository extends JpaRepository<FileLog, UUID> {
}
