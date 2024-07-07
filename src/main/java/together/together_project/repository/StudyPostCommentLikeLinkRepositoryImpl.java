package together.together_project.repository;

import static together.together_project.domain.QStudyPostCommentLikeLink.studyPostCommentLikeLink;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.StudyPostCommentLikeLink;

@Repository
@RequiredArgsConstructor
public class StudyPostCommentLikeLinkRepositoryImpl {

    private final JPAQueryFactory q;

    private final StudyPostCommentLikeLinkJpaRepository studyPostCommentLikeLinkRepository;

    public StudyPostCommentLikeLink save(StudyPostCommentLikeLink commentLike) {
        return studyPostCommentLikeLinkRepository.save(commentLike);
    }

    public Optional<StudyPostCommentLikeLink> findCommentLike(Long commentId, Long userId) {
        return q.select(studyPostCommentLikeLink)
                .from(studyPostCommentLikeLink)
                .where(studyPostCommentLikeLink.studyPostComment.id.eq(commentId)
                        .and(studyPostCommentLikeLink.user.id.eq(userId)))
                .stream()
                .findFirst();
    }
}
