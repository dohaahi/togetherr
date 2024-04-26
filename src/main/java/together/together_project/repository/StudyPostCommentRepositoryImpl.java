package together.together_project.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudyPostCommentRepositoryImpl {

    private final StudyPostCommentJpaRepository studyPostCommentRepository;
}
