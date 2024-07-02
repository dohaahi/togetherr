package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT_AND_ONE_MORE;
import static together.together_project.domain.QReviewPost.reviewPost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.ReviewPost;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

@Repository
@RequiredArgsConstructor
public class ReviewPostRepositoryImpl {

    private final JPAQueryFactory q;
    private final ReviewPostJpaRepository reviewPostRepository;

    public ReviewPost save(ReviewPost review) {
        return reviewPostRepository.save(review);
    }

    public Optional<ReviewPost> findReviewByReviewId(Long reviewId) {
        return q.select(reviewPost)
                .from(reviewPost)
                .where(reviewPost.id.eq(reviewId)
                        .and(reviewPost.deletedAt.isNull()))
                .stream()
                .findFirst();
    }

    public List<ReviewPost> paginateReviews(Long cursor) {
        if (null == cursor) {
            ReviewPost review = q.select(reviewPost)
                    .from(reviewPost)
                    .orderBy(reviewPost.id.desc())
                    .where(reviewPost.deletedAt.isNull())
                    .fetchFirst();

            if (review == null) {
                throw new CustomException(ErrorCode.DATA_NOT_FOUND);
            }

            cursor = review.getId() + 1L;
        }

        List<ReviewPost> reviews = q.select(reviewPost)
                .from(reviewPost)
                .orderBy(reviewPost.id.desc())
                .where(reviewPost.deletedAt.isNull()
                        .and(reviewPost.id.lt(cursor)))
                .limit(PAGINATION_COUNT_AND_ONE_MORE)
                .fetch();

        if (reviews.isEmpty()) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return reviews;
    }

    public Optional<ReviewPost> findReviewByStudyAndUser(Long studyId, Long userId) {
        return q.select(reviewPost)
                .from(reviewPost)
                .where(reviewPost.study.studyId.eq(studyId)
                        .and(reviewPost.author.id.eq(userId)))
                .stream()
                .findFirst();
    }
}
