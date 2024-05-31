package together.together_project.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import together.together_project.domain.Study;

public interface StudyJpaRepository extends JpaRepository<Study, Long> {

    @EntityGraph(attributePaths = "studyPost")
    @Query("select s from Study s where s.studyId < :after and s.deletedAt is null order by s.studyId desc limit :count")
    List<Study> paginateStudy(
            @Param("after") Long after,
            @Param("count") Long count
    );

    @Query("select s from Study s where s.studyId = :id and s.deletedAt is null")
    Optional<Study> findById(@Param("id") Long id);

    @Query("select s.studyId from Study s order by s.studyId desc limit 1")
    public Long findFirstByOrderByIdDesc();
}
