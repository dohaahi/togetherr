package together.together_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.ReviewComment;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.ReviewCommentService;
import together.together_project.service.dto.request.ReviewCommentCreateRequestDto;
import together.together_project.service.dto.request.ReviewCommentUpdatedRequestDto;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.ReviewCommentCreateResponseDto;
import together.together_project.service.dto.response.ReviewCommentUpdateResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews/{review-id}/comments")
public class ReviewCommentController {

    private final ReviewCommentService reviewCommentService;

    @PostMapping()
    public ResponseEntity<ResponseBody> writeComment(
            @PathVariable("review-id") Long reviewId,
            @Valid @RequestBody ReviewCommentCreateRequestDto request,
            @AuthUser User currentUser
    ) {
        ReviewComment reviewComment = reviewCommentService.writeComment(reviewId, request, currentUser);
        ReviewCommentCreateResponseDto response = ReviewCommentCreateResponseDto.of(reviewComment);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }

    @PutMapping("{review-comment-id}")
    public ResponseEntity<ResponseBody> updateComment(
            @PathVariable("review-id") Long reviewId,
            @PathVariable("review-comment-id") Long commentId,
            @Valid @RequestBody ReviewCommentUpdatedRequestDto request,
            @AuthUser User currentUser
    ) {
        verifyReviewCommentAuthor(commentId, currentUser);

        ReviewComment comment = reviewCommentService.updatedComment(reviewId, commentId, request);
        ReviewCommentUpdateResponseDto response = ReviewCommentUpdateResponseDto.of(comment);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    private void verifyReviewCommentAuthor(Long commentId, User currentUser) {
        ReviewComment comment = reviewCommentService.getByCommentId(commentId);

        if (!comment.getAuthor().equals(currentUser)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}
