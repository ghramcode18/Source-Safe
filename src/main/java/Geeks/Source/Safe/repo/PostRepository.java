package havebreak.SocialSphere.repo;


import havebreak.SocialSphere.Entity.Enum.PostEnum;
import havebreak.SocialSphere.Entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;


@Repository
public interface PostRepository extends JpaRepository<PostEntity, UUID> {
    List<PostEntity> findByType(PostEnum type);
    List<PostEntity> findByTypeOrderByCreatedAtAsc(PostEnum type);
    List<PostEntity> findByTypeOrderByCreatedAtDesc(PostEnum type);
}

