package together.together_project.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.StudyPostComment;

@Repository
@RequiredArgsConstructor
public class StudyPostCommentRepositoryImpl {

    private final StudyPostCommentJpaRepository studyPostCommentRepository;

    public StudyPostComment save(StudyPostComment comment) {
        return studyPostCommentRepository.save(comment);
    }
}
