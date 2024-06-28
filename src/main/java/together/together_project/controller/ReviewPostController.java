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
import together.together_project.domain.ReviewPost;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.ReviewPostService;
import together.together_project.service.dto.PaginationCollection;
import together.together_project.service.dto.PaginationResponseDto;
import together.together_project.service.dto.request.ReviewCreateRequestDto;
import together.together_project.service.dto.request.ReviewUpdateRequestDto;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.ReviewPostResponseDto;
import together.together_project.service.dto.response.ReviewResponseDto;
import together.together_project.service.dto.response.ReviewsResponseDto;

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

    @GetMapping()
    public ResponseEntity<ResponseBody> getAllReview(
            @RequestParam(value = "cursor", required = false) Long cursor
    ) {
        List<ReviewsResponseDto> reviews = reviewPostService.getAllReview(cursor)
                .stream()
                .map(ReviewsResponseDto::of)
                .toList();

        PaginationCollection<ReviewsResponseDto> collection = PaginationCollection.of(
                reviews, ReviewsResponseDto::reviewId);
        PaginationResponseDto<ReviewsResponseDto> response = PaginationResponseDto.of(
                collection);

        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("{review-post-id}")
    public ResponseEntity<ResponseBody> getReview(
            @PathVariable("review-post-id") Long reviewId
    ) {
        ReviewPost reviewPost = reviewPostService.getReview(reviewId);
        ReviewResponseDto response = ReviewResponseDto.of(reviewPost);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

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
