package together.together_project.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudyPostLikeLinkRepositoryImpl {

    private final StudyPostLikeLinkJpaRepository studyPostLikeLinkRepository;
}
