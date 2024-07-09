package together.together_project.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.ReviewLikeLink;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.User;
import together.together_project.repository.ReviewLikeLinkRepositoryImpl;
import together.together_project.service.dto.response.ReviewLikeResponseDto;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReviewPostService reviewPostService;

    private final ReviewLikeLinkRepositoryImpl reviewLikeLinkRepository;

    public ReviewLikeResponseDto like(Long reviewId, User user) {
        ReviewPost review = reviewPostService.getReview(reviewId);

        Optional<ReviewLikeLink> reviewLikeLink = reviewLikeLinkRepository.findReviewLike(reviewId, user.getId());

        if (reviewLikeLink.isEmpty()) {
            ReviewLikeLink reviewLike = ReviewLikeLink.builder()
                    .user(user)
                    .reviewPost(review)
                    .build();

            review.like();
            reviewLikeLinkRepository.save(reviewLike);
            return ReviewLikeResponseDto.of(reviewLike, true);
        }

        return withdrawReviewLike(reviewId, reviewLikeLink.get());
    }

    public List<ReviewLikeLink> getAllReviewLike(Long reviewId, Long cursor) {
        return reviewLikeLinkRepository.paginateReviewLike(reviewId, cursor);
    }

    public ReviewLikeResponseDto withdrawReviewLike(Long reviewId, ReviewLikeLink reviewLike) {
        reviewPostService.getReview(reviewId)
                .unlike();
        reviewLikeLinkRepository.deleteReviewLike(reviewLike.getId());

        return ReviewLikeResponseDto.of(reviewLike, false);
    }
}
