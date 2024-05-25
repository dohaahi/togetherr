package together.together_project.service;


import static together.together_project.validator.UserValidator.verifyLogin;
import static together.together_project.validator.UserValidator.verifySignup;
import static together.together_project.validator.UserValidator.verifyWithdraw;

import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.UserRepositoryImpl;
import together.together_project.service.dto.request.LoginRequestDto;
import together.together_project.service.dto.request.MyPageRequestDto;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.WithdrawRequestDto;
import together.together_project.service.dto.response.SignupResponseDto;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryImpl userRepository;
    private final BcryptService bcryptService;

    public SignupResponseDto signup(SignupRequestDto request) {

        Optional<User> userByEmail = userRepository.findByEmail(request.email());
        Optional<User> userByNickname = userRepository.findByNickname(request.nickname());

        verifySignup(userByEmail, userByNickname);

        String encodedPassword = bcryptService.encodeBcrypt(request.password());
        User hashedUser = request.toUser(encodedPassword);
        userRepository.save(hashedUser);

        return SignupResponseDto.from(hashedUser);
    }


    public Long login(LoginRequestDto request) {
        Optional<User> user = userRepository.findByEmail(request.email());

        // NOTE: 메서드 이름 verifyLogin OR checkEmptyUser
        verifyLogin(user);

        if (user.get().isDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        boolean matchedBcrypt = bcryptService.matchBcrypt(request.password(), user.get().getPassword());
        if (!matchedBcrypt) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        return user.get().getId();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public void withdraw(WithdrawRequestDto request, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        verifyWithdraw(user);

        if (user.get().isDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        boolean matchedBcrypt = bcryptService.matchBcrypt(request.password(), user.get().getPassword());
        if (!matchedBcrypt) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        user.get().softDelete();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public User updateMyPage(MyPageRequestDto request, Long userId) {
        User user = getUserById(userId);

        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        userRepository.findByEmail(request.email())
                .ifPresent(u -> {
                    throw new CustomException(ErrorCode.EMAIL_DUPLICATE);
                });

        userRepository.findByNickname(request.nickname())
                .ifPresent(u -> {
                    throw new CustomException(ErrorCode.NICKNAME_DUPLICATE);
                });

        return user.update(request);
    }
}
