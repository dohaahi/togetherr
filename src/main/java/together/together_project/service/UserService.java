package together.together_project.service;


import jakarta.transaction.Transactional;
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
        userRepository.findByEmail(request.email())
                .ifPresent(user -> {
                    throw new CustomException(ErrorCode.EMAIL_DUPLICATE);
                });

        userRepository.findByNickname(request.nickname())
                .ifPresent(user -> {
                    throw new CustomException(ErrorCode.NICKNAME_DUPLICATE);
                });

        String encodedPassword = bcryptService.encodeBcrypt(request.password());
        User hashedUser = request.toUser(encodedPassword);
        userRepository.save(hashedUser);

        return SignupResponseDto.from(hashedUser);
    }


    public Long login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTHENTICATION_FAILED));

        verifyUserPassword(
                request.password(),
                user.getPassword(),
                ErrorCode.AUTHENTICATION_FAILED
        );

        return user.getId();
    }

    public void withdraw(WithdrawRequestDto request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_VALIDATE));

        verifyUserPassword(
                request.password(),
                user.getPassword(),
                ErrorCode.PASSWORD_NOT_MATCH
        );

        user.softDelete();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public User updateMyPage(MyPageRequestDto request, Long userId) {
        User user = getUserById(userId);

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

    private void verifyUserPassword(
            String plainPassword,
            String hashPassword,
            ErrorCode errorCode
    ) {
        boolean matchedBcrypt = bcryptService.matchBcrypt(plainPassword, hashPassword);
        if (!matchedBcrypt) {
            throw new CustomException(errorCode);
        }
    }
}
