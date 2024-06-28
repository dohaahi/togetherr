package together.together_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.ReviewPostService;
import together.together_project.service.dto.request.ReviewCreateRequestDto;
import together.together_project.service.dto.request.ReviewUpdateRequestDto;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.ReviewPostResponseDto;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewPostController {

    private final ReviewPostService reviewPostService;

    @PostMapping()
    public ResponseEntity<ResponseBody> write(
            @Valid @RequestBody ReviewCreateRequestDto request,
            @AuthUser User currentUser
    ) {
        ReviewPost reviewPost = reviewPostService.write(request, currentUser);
        ReviewPostResponseDto response = ReviewPostResponseDto.of(reviewPost);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }

    @PutMapping("{review-post-id}")
    public ResponseEntity<ResponseBody> updateReview(
            @PathVariable("review-post-id") Long reviewId,
            @RequestBody ReviewUpdateRequestDto request,
            @AuthUser User currentUser
    ) {
        verifyReviewAuthor(reviewId, currentUser);

        ReviewPost reviewPost = reviewPostService.updateReview(reviewId, request);
        ReviewPostResponseDto response = ReviewPostResponseDto.of(reviewPost);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @DeleteMapping("{review-post-id}")
    public ResponseEntity<ResponseBody> withdrawReview(
            @PathVariable("review-post-id") Long reviewId,
            @AuthUser User currentUser
    ) {
        verifyReviewAuthor(reviewId, currentUser);

        reviewPostService.withdrawReview(reviewId);
        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    private void verifyReviewAuthor(Long reviewId, User currentUser) {
        ReviewPost review = reviewPostService.getReview(reviewId);

        if (!review.getAuthor().getId().equals(currentUser.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}
