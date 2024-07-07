package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT_AND_ONE_MORE;
import static together.together_project.domain.QStudyPostCommentLikeLink.studyPostCommentLikeLink;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.StudyPostCommentLikeLink;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

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

    public Optional<StudyPostCommentLikeLink> findCommentLike(Long commentLikeId) {
        return q.select(studyPostCommentLikeLink)
                .from(studyPostCommentLikeLink)
                .where(studyPostCommentLikeLink.id.eq(commentLikeId)
                        .and(studyPostCommentLikeLink.deletedAt.isNull()))
                .stream()
                .findFirst();
    }

    public List<StudyPostCommentLikeLink> paginateCommentLike(Long studyCommentId, Long cursor) {
        if (cursor == null) {
            StudyPostCommentLikeLink commentLike = q.select(studyPostCommentLikeLink)
                    .from(studyPostCommentLikeLink)
                    .where(studyPostCommentLikeLink.studyPostComment.id.eq(studyCommentId)
                            .and(studyPostCommentLikeLink.deletedAt.isNull()))
                    .fetchFirst();

            if (commentLike == null) {
                throw new CustomException(ErrorCode.DATA_NOT_FOUND);
            }

            cursor = commentLike.getId() + 1;
        }

        List<StudyPostCommentLikeLink> commentLikes = q.select(studyPostCommentLikeLink)
                .from(studyPostCommentLikeLink)
                .where(studyPostCommentLikeLink.studyPostComment.id.eq(studyCommentId)
                        .and(studyPostCommentLikeLink.deletedAt.isNull())
                        .and(studyPostCommentLikeLink.id.lt(cursor)))
                .limit(PAGINATION_COUNT_AND_ONE_MORE)
                .fetch();

        if (commentLikes == null) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return commentLikes;
    }

    public void deleteComment(Long commentLikeId) {
        q.delete(studyPostCommentLikeLink)
                .where(studyPostCommentLikeLink.id.eq(commentLikeId))
                .execute();
    }
}
