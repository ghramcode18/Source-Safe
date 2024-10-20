package havebreak.SocialSphere.repo;


import havebreak.SocialSphere.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByEmail (String email) ;

    Optional<UserEntity> findByUserName (String email) ;

}
