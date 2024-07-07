package together.together_project.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.ReviewComment;
import together.together_project.domain.ReviewCommentLikeLink;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.ReviewCommentLikeLinkRepositoryImpl;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommentLikeService {

    private final ReviewCommentService reviewCommentService;

    private final ReviewCommentLikeLinkRepositoryImpl reviewCommentLikeLinkRepository;
    private final ReviewPostService reviewPostService;

    public ReviewCommentLikeLink like(Long commentId, User user) {
        ReviewComment comment = reviewCommentService.getByCommentId(commentId);

        reviewCommentLikeLinkRepository.findCommentLike(commentId, user.getId())
                .ifPresent(reviewCommentLikeLink -> {
                    throw new CustomException(ErrorCode.INVALID_REQUEST);
                });

        ReviewCommentLikeLink commentLike = ReviewCommentLikeLink.builder()
                .user(user)
                .reviewComment(comment)
                .build();

        return reviewCommentLikeLinkRepository.save(commentLike);
    }

    public List<ReviewCommentLikeLink> getAllCommentLike(Long reviewId, Long commentId, Long cursor) {
        reviewPostService.getReview(reviewId);
        reviewCommentService.getByCommentId(commentId);

        return reviewCommentLikeLinkRepository.paginateCommentLike(commentId, cursor);
    }

    public void withdrawCommentLike(Long commentId, Long commentLikeId, User user) {
        ReviewComment comment = reviewCommentService.getByCommentId(commentId);

        if (!comment.getAuthor().equals(user)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        reviewCommentLikeLinkRepository.findCommentLike(commentLikeId)
                .orElseThrow(() -> new CustomException(ErrorCode.LIKE_LINK_NOT_FOUND));

        reviewCommentLikeLinkRepository.deletedCommentLike(commentLikeId);
    }
}
