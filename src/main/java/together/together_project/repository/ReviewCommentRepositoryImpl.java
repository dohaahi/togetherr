package together.together_project.repository;


import static together.together_project.constant.StudyConstant.PAGINATION_COUNT_AND_ONE_MORE;
import static together.together_project.domain.QReviewComment.reviewComment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.ReviewComment;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

@Repository
@RequiredArgsConstructor
public class ReviewCommentRepositoryImpl {

    private final JPAQueryFactory q;
    private final ReviewCommentJpaRepository reviewCommentRepository;

    public ReviewComment save(ReviewComment comment) {
        return reviewCommentRepository.save(comment);
    }

    public Optional<ReviewComment> findByCommentId(Long commentId) {
        return q.select(reviewComment)
                .from(reviewComment)
                .where(reviewComment.id.eq(commentId)
                        .and(reviewComment.deletedAt.isNull()))
                .stream()
                .findFirst();
    }

    public List<ReviewComment> paginateComment(Long reviewId, Long cursor) {
        if (cursor == null) {
            ReviewComment comment = q.select(reviewComment)
                    .from(reviewComment)
                    .orderBy(reviewComment.id.desc())
                    .where(reviewComment.reviewPost.id.eq(reviewId)
                            .and(reviewComment.parentCommentId.isNull())
                            .and(reviewComment.deletedAt.isNull()))
                    .fetchFirst();

            if (comment == null) {
                throw new CustomException(ErrorCode.DATA_NOT_FOUND);
            }

            cursor = comment.getId() + 1;
        }

        List<ReviewComment> comments = q.select(reviewComment)
                .from(reviewComment)
                .orderBy(reviewComment.id.desc())
                .where(reviewComment.reviewPost.id.eq(reviewId)
                        .and(reviewComment.parentCommentId.isNull())
                        .and(reviewComment.deletedAt.isNull())
                        .and(reviewComment.id.lt(cursor)))
                .limit(PAGINATION_COUNT_AND_ONE_MORE)
                .fetch();

        if (comments.isEmpty()) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return comments;
    }

    public List<ReviewComment> paginateChildComment(Long reviewId, Long reviewCommentId, Long cursor) {
        if (cursor == null) {
            ReviewComment comment = q.select(reviewComment)
                    .from(reviewComment)
                    .orderBy(reviewComment.id.desc())
                    .where(reviewComment.reviewPost.id.eq(reviewId)
                            .and(reviewComment.parentCommentId.eq(reviewCommentId))
                            .and(reviewComment.deletedAt.isNull()))
                    .fetchFirst();

            if (comment == null) {
                throw new CustomException(ErrorCode.DATA_NOT_FOUND);
            }

            cursor = comment.getId() + 1;
        }

        List<ReviewComment> comments = q.select(reviewComment)
                .from(reviewComment)
                .orderBy(reviewComment.id.desc())
                .where(reviewComment.reviewPost.id.eq(reviewId)
                        .and(reviewComment.parentCommentId.eq(reviewCommentId))
                        .and(reviewComment.deletedAt.isNull())
                        .and(reviewComment.id.lt(cursor)))
                .limit(PAGINATION_COUNT_AND_ONE_MORE)
                .fetch();

        if (comments.isEmpty()) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return comments;
    }
}
