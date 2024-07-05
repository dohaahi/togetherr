package together.together_project.controller;

import static together.together_project.constant.UserConstant.ACCESS_TOKEN;
import static together.together_project.constant.UserConstant.ROOT_PATH;
import static together.together_project.constant.UserConstant.SET_COOKIE;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.User;
import together.together_project.security.JwtProvider;
import together.together_project.service.UserService;
import together.together_project.service.dto.PaginationResponseDtoOfUserReviews;
import together.together_project.service.dto.PaginationResponseDtoOfUserStudies;
import together.together_project.service.dto.TokenDto;
import together.together_project.service.dto.request.LoginRequestDto;
import together.together_project.service.dto.request.MyPageRequestDto;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.WithdrawRequestDto;
import together.together_project.service.dto.response.MyPageResponseDto;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.SignupResponseDto;
import together.together_project.service.dto.response.UserReviewsResponseDto;
import together.together_project.service.dto.response.UserReviewsResponseDto.MetaReview;
import together.together_project.service.dto.response.UserStudiesResponseDto;
import together.together_project.service.dto.response.UserStudiesResponseDto.MetaStudy;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/auth/signup")
    public ResponseEntity<ResponseBody> signup(@Valid @RequestBody SignupRequestDto request) {
        SignupResponseDto response = userService.signup(request);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseBody> login(@Valid @RequestBody LoginRequestDto request) {
        Long loggedInUserId = userService.login(request);

        TokenDto tokenDto = new TokenDto(jwtProvider.createAccessToken(loggedInUserId));
        ResponseBody body = new ResponseBody(tokenDto, null, HttpStatus.OK.value());
        ResponseCookie accessToken = createCookieFromToken(tokenDto.accessToken());

        return ResponseEntity.status(HttpStatus.OK)
                .header(SET_COOKIE, accessToken.toString())
                .body(body);
    }

    @DeleteMapping("/users/withdraw")
    public ResponseEntity<ResponseBody> withdraw(
            @RequestBody WithdrawRequestDto request,
            @AuthUser User currentUser
    ) {
        userService.withdraw(request, currentUser.getId());
        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/users/mypage")
    public ResponseEntity<ResponseBody> getMyPage(
            @AuthUser User currentUser
    ) {
        User user = userService.getUserById(currentUser.getId());
        MyPageResponseDto response = MyPageResponseDto.from(user);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PutMapping("/users/mypage")
    public ResponseEntity<ResponseBody> updateMyPage(
            @Valid @RequestBody MyPageRequestDto request,
            @AuthUser User currentUser
    ) {
        User user = userService.updateMyPage(request, currentUser.getId());
        MyPageResponseDto response = MyPageResponseDto.from(user);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/users/{user-id}/studies")
    public ResponseEntity<ResponseBody> getUserStudies(
            @PathVariable("user-id") Long userId,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @AuthUser User currentUser
    ) {
        UserStudiesResponseDto studies = userService.getUserStudies(userId, cursor);
        PaginationResponseDtoOfUserStudies response = PaginationResponseDtoOfUserStudies.of(studies,
                MetaStudy::studyId);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/users/{user-id}/reviews")
    public ResponseEntity<ResponseBody> getUserReviews(
            @PathVariable("user-id") Long userId,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @AuthUser User CurrentUser
    ) {
        UserReviewsResponseDto reviews = userService.getUserReviews(userId, cursor);
        PaginationResponseDtoOfUserReviews response = PaginationResponseDtoOfUserReviews.of(reviews,
                MetaReview::reviewId);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    private ResponseCookie createCookieFromToken(String tokenValue) {
        return ResponseCookie.from(ACCESS_TOKEN, tokenValue)
                .httpOnly(true)
                .secure(true)
                .path(ROOT_PATH)
                .build();
    }
}
