package together.together_project.repository;

import static together.together_project.domain.QReviewPost.reviewPost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.ReviewPost;

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
}
