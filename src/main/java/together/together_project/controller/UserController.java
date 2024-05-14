package together.together_project.controller;

import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.User;
import together.together_project.security.JwtProvider;
import together.together_project.service.UserService;
import together.together_project.service.dto.TokenDto;
import together.together_project.service.dto.request.LoginRequestDto;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.WithdrawRequestDto;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.SignupResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Validated
public class UserController {

    private static final String SET_COOKIE = "set-Cookie";
    private static final String ACCESS_TOKEN = "accessToken";

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/signup")
    public ResponseEntity<ResponseBody> signup(@Valid @RequestBody SignupRequestDto request) {
        SignupResponseDto response = userService.signup(request);

        ResponseBody body = new ResponseBody(response, null, 201);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseBody> login(@Valid @RequestBody LoginRequestDto request) {
        userService.login(request);

        User user = userService.getUserByEmail(request.getEmail());

        TokenDto tokenDto = new TokenDto(jwtProvider.createAccessToken(user.getId()));

        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

        ResponseCookie accessToken = createCookieFromToken(ACCESS_TOKEN, tokenDto.accessToken());

        return ResponseEntity.status(HttpStatus.OK)
                .header(SET_COOKIE, accessToken.toString())
                .body(body);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<ResponseBody> withdraw(
            @RequestBody WithdrawRequestDto request,
            @CookieValue(name = "accessToken") Cookie cookie
    ) {

        Long userId = jwtProvider.verifyAuthTokenOrThrow(cookie.getValue());

        userService.withdraw(request, userId);

        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

        // TODO: 코드는 NO_CONTENT 인데 데이터를 넘겨도 되는지
        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    private ResponseCookie createCookieFromToken(String tokenName, String tokenValue) {
        return ResponseCookie.from(tokenName, tokenValue)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .build();
    }
}
