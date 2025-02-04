package together.together_project.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.ReviewComment;
import together.together_project.domain.User;
import together.together_project.service.ReviewCommentLikeService;
import together.together_project.service.ReviewCommentService;
import together.together_project.service.dto.PaginationCollection;
import together.together_project.service.dto.PaginationResponseDto;
import together.together_project.service.dto.request.ReviewCommentCreateRequestDto;
import together.together_project.service.dto.request.ReviewCommentUpdatedRequestDto;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.ReviewCommentCreateResponseDto;
import together.together_project.service.dto.response.ReviewCommentLikeResponseDto;
import together.together_project.service.dto.response.ReviewCommentLikesResponseDto;
import together.together_project.service.dto.response.ReviewCommentUpdateResponseDto;
import together.together_project.service.dto.response.ReviewCommentsResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews/{review-id}/comments")
public class ReviewCommentController {

    private final ReviewCommentService reviewCommentService;
    private final ReviewCommentLikeService reviewCommentLikeService;

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

    @GetMapping()
    public ResponseEntity<ResponseBody> getAllComment(
            @PathVariable("review-id") Long reviewId,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @AuthUser User currentUser
    ) {
        List<ReviewCommentsResponseDto> comments = reviewCommentService.getAllComment(reviewId, cursor)
                .stream()
                .map(ReviewCommentsResponseDto::of)
                .toList();

        PaginationCollection<ReviewCommentsResponseDto> collection = PaginationCollection.of(
                comments, ReviewCommentsResponseDto::id);
        PaginationResponseDto<ReviewCommentsResponseDto> response = PaginationResponseDto.of(
                collection);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/{parent-comment-id}")
    public ResponseEntity<ResponseBody> getAllChildComment(
            @PathVariable("review-id") Long reviewId,
            @PathVariable("parent-comment-id") Long reviewCommentId,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @AuthUser User currentUser
    ) {
        List<ReviewCommentsResponseDto> comments = reviewCommentService.getAllChildComment(reviewId, reviewCommentId,
                        cursor)
                .stream()
                .map(ReviewCommentsResponseDto::of)
                .toList();

        PaginationCollection<ReviewCommentsResponseDto> collection = PaginationCollection.of(comments,
                ReviewCommentsResponseDto::id);
        PaginationResponseDto<ReviewCommentsResponseDto> response = PaginationResponseDto.of(
                collection);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PutMapping("/{review-comment-id}")
    public ResponseEntity<ResponseBody> updateComment(
            @PathVariable("review-id") Long reviewId,
            @PathVariable("review-comment-id") Long commentId,
            @Valid @RequestBody ReviewCommentUpdatedRequestDto request,
            @AuthUser User currentUser
    ) {
        ReviewComment comment = reviewCommentService.updateComment(reviewId, commentId, request, currentUser);
        ReviewCommentUpdateResponseDto response = ReviewCommentUpdateResponseDto.of(comment);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @DeleteMapping("/{review-comment-id}")
    public ResponseEntity<ResponseBody> withdrawComment(
            @PathVariable("review-id") Long reviewId,
            @PathVariable("review-comment-id") Long commentId,
            @AuthUser User currentUser
    ) {
        reviewCommentService.withdrawComment(reviewId, commentId, currentUser);
        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PostMapping("/{parent-comment-id}")
    public ResponseEntity<ResponseBody> writeChildComment(
            @PathVariable("review-id") Long reviewId,
            @PathVariable("parent-comment-id") Long commentId,
            @Valid @RequestBody ReviewCommentCreateRequestDto request,
            @AuthUser User currentUser
    ) {
        ReviewComment comment = reviewCommentService.writeChildComment(reviewId, commentId, request, currentUser);

        ReviewCommentCreateResponseDto response = ReviewCommentCreateResponseDto.of(comment);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }

    @PutMapping("/{parent-comment-id}/{child-comment-id}")
    public ResponseEntity<ResponseBody> updateChildComment(
            @PathVariable("review-id") Long reviewId,
            @PathVariable("parent-comment-id") Long parentCommentId,
            @PathVariable("child-comment-id") Long childCommentId,
            @RequestBody ReviewCommentUpdatedRequestDto request,
            @AuthUser User currentUser

    ) {
        ReviewComment comment = reviewCommentService.updateChildComment(reviewId, parentCommentId, childCommentId,
                request, currentUser);
        ReviewCommentUpdateResponseDto response = ReviewCommentUpdateResponseDto.of(comment);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @DeleteMapping("/{parent-comment-id}/{child-comment-id}")
    public ResponseEntity<ResponseBody> withdrawChildComment(
            @PathVariable("review-id") Long reviewId,
            @PathVariable("parent-comment-id") Long parentCommentId,
            @PathVariable("child-comment-id") Long childCommentId,
            @AuthUser User CurrentUser
    ) {
        reviewCommentService.withdrawChildComment(reviewId, parentCommentId, childCommentId, CurrentUser);
        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PostMapping("/{review-comment-id}/likes")
    public ResponseEntity<ResponseBody> commentLike(
            @PathVariable("review-id") Long reviewId,
            @PathVariable("review-comment-id") Long commentId,
            @AuthUser User currentUser
    ) {
        ReviewCommentLikeResponseDto response = reviewCommentLikeService.like(reviewId, commentId, currentUser);

        if (response.hasLike()) {
            ResponseBody body = new ResponseBody(response, null, HttpStatus.CREATED.value());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(body);
        }

        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/{review-comment-id}/likes")
    public ResponseEntity<ResponseBody> getAllCommentLike(
            @PathVariable("review-id") Long reviewId,
            @PathVariable("review-comment-id") Long commentId,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @AuthUser User currentUser
    ) {
        List<ReviewCommentLikesResponseDto> likes = reviewCommentLikeService.getAllCommentLike(reviewId, commentId,
                        cursor)
                .stream()
                .map(ReviewCommentLikesResponseDto::of)
                .toList();

        PaginationCollection<ReviewCommentLikesResponseDto> collection = PaginationCollection.of(
                likes, ReviewCommentLikesResponseDto::id);
        PaginationResponseDto<ReviewCommentLikesResponseDto> response = PaginationResponseDto.of(
                collection);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }
}
