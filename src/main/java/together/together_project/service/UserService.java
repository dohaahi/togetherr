package together.together_project.service;


import static together.together_project.exception.ErrorCode.EMAIL_REGEX;
import static together.together_project.exception.ErrorCode.PASSWORD_REGEX;

import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.UserRepositoryImpl;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.response.SignupResponseDto;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryImpl userRepository;
    private final BcryptService bcryptService;

    public SignupResponseDto signup(SignupRequestDto request) {

        verifyEmailRegex(request);
        verifyEmailDuplicate(request);
        verifyNicknameLength(request);
        verifyNicknameDuplicate(request);
        verifyPasswordRegex(request);

        String encodedPassword = bcryptService.encodeBcrypt(request.getPassword());
        User newUser = request.toUser(encodedPassword);
        userRepository.save(newUser);

        return SignupResponseDto.from(newUser);
    }

    private void verifyEmailRegex(SignupRequestDto request) {
        if (!Pattern.matches(EMAIL_REGEX, request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_FORMAT_INVALID);
        }
    }

    private void verifyEmailDuplicate(SignupRequestDto request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATE);
        }
    }

    private void verifyNicknameLength(SignupRequestDto request) {
        if (request.getNickname().isEmpty()) {
            throw new CustomException(ErrorCode.NICKNAME_LENGTH);
        }
    }

    private void verifyNicknameDuplicate(SignupRequestDto request) {
        Optional<User> user = userRepository.findByNickname(request.getNickname());

        if (user.isPresent()) {
            throw new CustomException(ErrorCode.NICKNAME_DUPLICATE);
        }
    }

    private void verifyPasswordRegex(SignupRequestDto request) {
        if (!Pattern.matches(PASSWORD_REGEX, request.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_FORMAT_VALIDATE);
        }
    }
}
