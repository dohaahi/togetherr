package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT_AND_ONE_MORE;
import static together.together_project.domain.QReviewLikeLink.reviewLikeLink;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.ReviewLikeLink;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

@Repository
@RequiredArgsConstructor
public class ReviewLikeLinkRepositoryImpl {

    private final JPAQueryFactory q;

    private final ReviewLikeLinkJpaRepository reviewLikeLinkRepository;

    public ReviewLikeLink save(ReviewLikeLink reviewLike) {
        return reviewLikeLinkRepository.save(reviewLike);
    }

    public Optional<ReviewLikeLink> findReviewLike(Long reviewId, Long userId) {
        return q.select(reviewLikeLink)
                .from(reviewLikeLink)
                .where(reviewLikeLink.reviewPost.id.eq(reviewId)
                        .and(reviewLikeLink.user.id.eq(userId)))
                .stream()
                .findFirst();
    }

    public List<ReviewLikeLink> paginateReviewLike(Long reviewId, Long cursor) {
        if (cursor == null) {
            ReviewLikeLink reviewLike = q.select(reviewLikeLink)
                    .from(reviewLikeLink)
                    .where(reviewLikeLink.reviewPost.id.eq(reviewId)
                            .and(reviewLikeLink.deletedAt.isNull()))
                    .fetchFirst();

            if (reviewLike == null) {
                throw new CustomException(ErrorCode.DATA_NOT_FOUND);
            }

            cursor = reviewLike.getId() + 1;
        }

        List<ReviewLikeLink> reviewLikes = q.select(reviewLikeLink)
                .from(reviewLikeLink)
                .where(reviewLikeLink.reviewPost.id.eq(reviewId)
                        .and(reviewLikeLink.deletedAt.isNull())
                        .and(reviewLikeLink.id.lt(cursor)))
                .limit(PAGINATION_COUNT_AND_ONE_MORE)
                .fetch();

        if (reviewLikes == null) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return reviewLikes;
    }
}
