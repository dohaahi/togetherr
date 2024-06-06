package together.together_project.repository;

import static together.together_project.domain.QStudyPostComment.studyPostComment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.StudyPostComment;

@Repository
@RequiredArgsConstructor
public class StudyPostCommentRepositoryImpl {

    private final JPAQueryFactory q;
    private final StudyPostCommentJpaRepository studyPostCommentRepository;

    public StudyPostComment save(StudyPostComment comment) {
        return studyPostCommentRepository.save(comment);
    }

    public Optional<StudyPostComment> findCommentById(Long commentId) {
        return q.select(studyPostComment)
                .from(studyPostComment)
                .where(studyPostComment.deletedAt.isNull())
                .where(studyPostComment.id.eq(commentId))
                .stream()
                .findFirst();
    }
}
