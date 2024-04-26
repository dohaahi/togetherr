package together.together_project.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewCommentLikeLinkRepositoryImpl {

    private final ReviewCommentLikeLinkJpaRepository reviewCommentLikeLinkRepository;
}
