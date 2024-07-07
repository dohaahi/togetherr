package together.together_project.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.ReviewLikeLink;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.ReviewLikeLinkRepositoryImpl;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReviewPostService reviewPostService;

    private final ReviewLikeLinkRepositoryImpl reviewLikeLinkRepository;

    public ReviewLikeLink like(Long reviewId, User user) {
        ReviewPost review = reviewPostService.getReview(reviewId);

        reviewLikeLinkRepository.findReviewLike(reviewId, user.getId())
                .ifPresent(reviewLikeLink -> {
                    throw new CustomException(ErrorCode.INVALID_REQUEST);
                });

        ReviewLikeLink reviewLike = ReviewLikeLink.builder()
                .user(user)
                .reviewPost(review)
                .build();

        return reviewLikeLinkRepository.save(reviewLike);
    }

    public List<ReviewLikeLink> getAllReviewLike(Long reviewId, Long cursor) {
        return reviewLikeLinkRepository.paginateReviewLike(reviewId, cursor);
    }

    public void withdrawReviewLike(Long reviewId, Long reviewLikeId, User user) {
        reviewPostService.getReview(reviewId);
        ReviewLikeLink reviewLike = reviewLikeLinkRepository.findReviewLike(reviewLikeId)
                .orElseThrow(() -> new CustomException(ErrorCode.LIKE_LINK_NOT_FOUND));

        if (!reviewLike.getUser().equals(user)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        reviewLikeLinkRepository.deleteReviewLike(reviewLikeId);
    }
}
