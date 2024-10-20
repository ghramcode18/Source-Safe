package havebreak.SocialSphere.repo;


import havebreak.SocialSphere.Entity.Enum.PostEnum;
import havebreak.SocialSphere.Entity.LikeEntity;
import havebreak.SocialSphere.Entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, UUID> {
}

