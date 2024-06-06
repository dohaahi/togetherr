package together.together_project.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import together.together_project.domain.UserStudyLink;

public interface UserStudyLinkJpaRepository extends JpaRepository<UserStudyLink, Long> {

    @Query("select usl from UserStudyLink usl where  usl.study.studyId = :studyId and usl.participant.id = :userId")
    Optional<UserStudyLink> findByStudyIdAndUserId(@Param("studyId") Long studyId, @Param("userId") Long userId);

    @Query("select usl from UserStudyLink usl "
            + "where usl.study.studyId = :studyId and usl.id < :after and usl.deletedAt is not null "
            + "order by usl.id desc limit :count")
    List<UserStudyLink> paginateJoinRequest(Long after, Long count, Long studyId);

    @Query("select usl.id from UserStudyLink usl order by usl.id desc limit 1")
    Long findFirstOderByIdDesc();
}
