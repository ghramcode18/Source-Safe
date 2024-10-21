package Geeks.Source.Safe.repo;

import Geeks.Source.Safe.Entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
}