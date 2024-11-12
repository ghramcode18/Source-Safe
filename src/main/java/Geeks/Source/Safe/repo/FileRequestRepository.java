package Geeks.Source.Safe.repo;

import java.util.UUID;
import Geeks.Source.Safe.Entity.FileRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRequestRepository extends JpaRepository<FileRequest, UUID> {

}
