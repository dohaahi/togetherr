package together.together_project.repository;

import static together.together_project.domain.QReviewLikeLink.reviewLikeLink;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.ReviewLikeLink;

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
}
