package together.together_project.service;

import java.util.List;
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

        ReviewComment comment = request.toReviewComment(review, user);

        return reviewCommentRepository.save(comment);
    }

    public ReviewComment getByCommentId(Long commentId) {
        return reviewCommentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public ReviewComment updatedComment(Long reviewId, Long commentId, ReviewCommentUpdatedRequestDto request) {
        reviewPostService.getReview(reviewId);

        if (request.content().trim().isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_CONTENT_ERROR);
        }

        return getByCommentId(commentId)
                .update(request);
    }

    public void withdrawComment(Long reviewId, Long commentId) {
        reviewPostService.getReview(reviewId);

        getByCommentId(commentId)
                .softDelete();
    }

    public List<ReviewComment> getAllComment(Long reviewId, Long cursor) {
        reviewPostService.getReview(reviewId);

        return reviewCommentRepository.paginateComment(reviewId, cursor);
    }

    public List<ReviewComment> getAllChildComment(Long reviewId, Long commentId, Long cursor) {
        reviewPostService.getReview(reviewId);
        reviewCommentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        return reviewCommentRepository.paginateChildComment(reviewId, commentId, cursor);
    }

    public ReviewComment writeChildComment(Long reviewId, Long commentId, ReviewCommentCreateRequestDto request,
                                           User user
    ) {
        ReviewPost review = reviewPostService.getReview(reviewId);
        checkParentCommentAndCheckCommentDeleted(commentId);

        ReviewComment childComment = ReviewComment.builder()
                .author(user)
                .reviewPost(review)
                .content(request.content())
                .parentCommentId(commentId)
                .build();

        return reviewCommentRepository.save(childComment);
    }

    public ReviewComment updateChildComment(Long reviewId,
                                            Long parentCommentId,
                                            Long childCommentId,
                                            ReviewCommentUpdatedRequestDto request
    ) {
        reviewPostService.getReview(reviewId);
        checkParentCommentAndCheckCommentDeleted(parentCommentId);

        return checkChildCommentAndGet(parentCommentId, childCommentId)
                .update(request);
    }

    public void withdrawChildComment(Long reviewId, Long parentCommentId, Long childCommentId) {
        reviewPostService.getReview(reviewId);
        checkParentCommentAndCheckCommentDeleted(parentCommentId);

        checkChildCommentAndGet(parentCommentId, childCommentId)
                .softDelete();
    }

    private void checkParentCommentAndCheckCommentDeleted(Long commentId) {
        ReviewComment comment = reviewCommentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.getParentCommentId() != null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    private ReviewComment checkChildCommentAndGet(Long parentCommentId, Long childCommentId) {
        ReviewComment comment = reviewCommentRepository.findByCommentId(childCommentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.getParentCommentId() == null || !comment.getParentCommentId().equals(parentCommentId)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return comment;
    }

    private void isParentComment(Long parentCommentId, ReviewComment childComment) {
        if (!childComment.getParentCommentId().equals(parentCommentId)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }
}
