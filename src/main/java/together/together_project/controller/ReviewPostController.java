package together.together_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.User;
import together.together_project.service.ReviewPostService;
import together.together_project.service.dto.request.ReviewCreateRequestDto;
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
}
