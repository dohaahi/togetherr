package together.together_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.ReviewComment;
import together.together_project.domain.User;
import together.together_project.service.ReviewCommentService;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.ReviewCommentCreateResponseDto;

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
}
