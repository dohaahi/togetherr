package together.together_project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import together.together_project.domain.Study;

public interface StudyJpaRepository extends JpaRepository<Study, Long> {

    @EntityGraph(attributePaths = "studyPost")
    @Query("select s from Study s where s.id > :after order by s.id desc limit :count")
    List<Study> paginateStudy(
            @Param("after") Long after,
            @Param("count") Long count
    );
}
