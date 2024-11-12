package Geeks.Source.Safe.repo;

import Geeks.Source.Safe.Entity.ReservationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, UUID> {

        ReservationHistory findByTextFileIdAndCheckOutStatusNullAndCheckOutEndTimeNull(UUID textFileId);

        List<ReservationHistory> findByExpirationTimeBeforeAndCheckOutStatusIsNull(LocalDateTime currentTime);

        List<ReservationHistory> findByUserId(UUID userId);
        List<ReservationHistory>  findByTextFileId(UUID textFileId);

        boolean existsByUser_IdAndTextFile_IdAndCheckOutStatusIsNull(UUID userId, UUID textFileId);
    }
