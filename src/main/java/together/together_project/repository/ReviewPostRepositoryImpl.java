package together.together_project.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.ReviewPost;

@Repository
@RequiredArgsConstructor
public class ReviewPostRepositoryImpl {

    private final ReviewPostJpaRepository reviewPostRepository;

    public ReviewPost save(ReviewPost review) {
        return reviewPostRepository.save(review);
    }
}
