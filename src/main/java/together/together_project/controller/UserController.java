package together.together_project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.service.UserService;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.SignupResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseBody> signup(@RequestBody SignupRequestDto request) {
        SignupResponseDto response = userService.signup(request);

        ResponseBody body = new ResponseBody(response, null, 201);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }
}
