package together.together_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.ReviewComment;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.ReviewCommentRepositoryImpl;
import together.together_project.service.dto.request.ReviewCommentCreateRequestDto;
import together.together_project.service.dto.request.ReviewCommentUpdatedRequestDto;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommentService {

    private final ReviewPostService reviewPostService;

    private final ReviewCommentRepositoryImpl reviewCommentRepository;

    public ReviewComment writeComment(Long reviewId, ReviewCommentCreateRequestDto request, User user) {
        ReviewPost review = reviewPostService.getReview(reviewId);

        if (request.content().trim().isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_CONTENT_ERROR);
        }

        ReviewComment comment = ReviewComment.builder()
                .author(user)
                .reviewPost(review)
                .content(request.content())
                .build();

        return reviewCommentRepository.save(comment);
    }

    public ReviewComment getByCommentId(Long commentId) {
        return reviewCommentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    }

    public ReviewComment updatedComment(Long reviewId, Long commentId, ReviewCommentUpdatedRequestDto request) {
        reviewPostService.getReview(reviewId);

        if (request.content().trim().isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_CONTENT_ERROR);
        }

        return getByCommentId(commentId)
                .update(request);
    }
}
