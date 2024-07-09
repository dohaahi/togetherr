package together.together_project.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.ReviewComment;
import together.together_project.domain.ReviewCommentLikeLink;
import together.together_project.domain.User;
import together.together_project.repository.ReviewCommentLikeLinkRepositoryImpl;
import together.together_project.service.dto.response.ReviewCommentLikeResponseDto;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommentLikeService {

    private final ReviewPostService reviewPostService;
    private final ReviewCommentService reviewCommentService;

    private final ReviewCommentLikeLinkRepositoryImpl reviewCommentLikeLinkRepository;

    public ReviewCommentLikeResponseDto like(Long reviewId, Long commentId, User user) {
        reviewPostService.getReview(reviewId);

        ReviewComment comment = reviewCommentService.getByCommentId(commentId);

        Optional<ReviewCommentLikeLink> commentLikeLink = reviewCommentLikeLinkRepository.findCommentLike(
                commentId,
                user.getId());

        if (commentLikeLink.isEmpty()) {
            ReviewCommentLikeLink commentLike = ReviewCommentLikeLink.builder()
                    .user(user)
                    .reviewComment(comment)
                    .build();

            comment.like();
            reviewCommentLikeLinkRepository.save(commentLike);
            return ReviewCommentLikeResponseDto.of(commentLike, true);
        }

        return withdrawCommentLike(commentId, commentLikeLink.get());
    }

    public List<ReviewCommentLikeLink> getAllCommentLike(Long reviewId, Long commentId, Long cursor) {
        reviewPostService.getReview(reviewId);
        reviewCommentService.getByCommentId(commentId);

        return reviewCommentLikeLinkRepository.paginateCommentLike(commentId, cursor);
    }

    public ReviewCommentLikeResponseDto withdrawCommentLike(Long commentId, ReviewCommentLikeLink commentLike) {
        reviewCommentService.getByCommentId(commentId)
                .unlike();
        reviewCommentLikeLinkRepository.deletedCommentLike(commentLike.getId());

        return ReviewCommentLikeResponseDto.of(commentLike, false);
    }
}
