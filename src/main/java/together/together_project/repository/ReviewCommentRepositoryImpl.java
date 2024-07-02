package together.together_project.repository;


import static together.together_project.domain.QReviewComment.reviewComment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.ReviewComment;

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
}
