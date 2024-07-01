package together.together_project.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.ReviewComment;

@Repository
@RequiredArgsConstructor
public class ReviewCommentRepositoryImpl {

    private final ReviewCommentJpaRepository reviewCommentRepository;

    public ReviewComment save(ReviewComment comment) {
        return reviewCommentRepository.save(comment);
    }
}
