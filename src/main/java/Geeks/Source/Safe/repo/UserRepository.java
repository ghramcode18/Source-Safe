package Geeks.Source.Safe.repo;

import Geeks.Source.Safe.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail (String email) ;

    Optional<User> findByUserName (String email) ;

    List<User> findByUserNameContainingIgnoreCaseOrFullNameContainingIgnoreCase(String searchTerm, String searchTerm1);
}
