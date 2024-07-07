package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT_AND_ONE_MORE;
import static together.together_project.domain.QReviewCommentLikeLink.reviewCommentLikeLink;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.ReviewCommentLikeLink;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

@Repository
@RequiredArgsConstructor
public class ReviewCommentLikeLinkRepositoryImpl {

    private final JPAQueryFactory q;

    private final ReviewCommentLikeLinkJpaRepository reviewCommentLikeLinkRepository;

    public Optional<ReviewCommentLikeLink> findCommentLike(Long commentId, Long userId) {
        return q.select(reviewCommentLikeLink)
                .from(reviewCommentLikeLink)
                .where(reviewCommentLikeLink.reviewComment.id.eq(commentId)
                        .and(reviewCommentLikeLink.user.id.eq(userId)))
                .stream()
                .findFirst();
    }

    public ReviewCommentLikeLink save(ReviewCommentLikeLink commentLike) {
        return reviewCommentLikeLinkRepository.save(commentLike);
    }

    public List<ReviewCommentLikeLink> paginateCommentLike(Long commentId, Long cursor) {
        if (cursor == null) {
            ReviewCommentLikeLink commentLike = q.select(reviewCommentLikeLink)
                    .from(reviewCommentLikeLink)
                    .where(reviewCommentLikeLink.reviewComment.id.eq(commentId)
                            .and(reviewCommentLikeLink.deletedAt.isNull()))
                    .fetchFirst();

            if (commentLike == null) {
                throw new CustomException(ErrorCode.DATA_NOT_FOUND);
            }

            cursor = commentLike.getId() + 1;
        }

        List<ReviewCommentLikeLink> commentLikes = q.select(reviewCommentLikeLink)
                .from(reviewCommentLikeLink)
                .where(reviewCommentLikeLink.reviewComment.id.eq(commentId)
                        .and(reviewCommentLikeLink.id.lt(cursor))
                        .and(reviewCommentLikeLink.deletedAt.isNull()))
                .limit(PAGINATION_COUNT_AND_ONE_MORE)
                .fetch();

        if (commentLikes == null) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return commentLikes;
    }

    public Optional<ReviewCommentLikeLink> findCommentLike(Long commentLikeId) {
        return q.select(reviewCommentLikeLink)
                .from(reviewCommentLikeLink)
                .where(reviewCommentLikeLink.id.eq(commentLikeId)
                        .and(reviewCommentLikeLink.deletedAt.isNull()))
                .stream()
                .findFirst();
    }

    public void deletedCommentLike(Long commentLikeId) {
        q.delete(reviewCommentLikeLink)
                .where(reviewCommentLikeLink.id.eq(commentLikeId))
                .execute();
    }
}
