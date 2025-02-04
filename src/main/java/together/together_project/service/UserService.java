package together.together_project.service;


import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.User;
import together.together_project.domain.UserStudyLink;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.UserRepositoryImpl;
import together.together_project.service.dto.request.LoginRequestDto;
import together.together_project.service.dto.request.MyPageRequestDto;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.WithdrawRequestDto;
import together.together_project.service.dto.response.SignupResponseDto;
import together.together_project.service.dto.response.UserReviewsResponseDto;
import together.together_project.service.dto.response.UserStudiesResponseDto;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryImpl userRepository;
    private final BcryptService bcryptService;
    private final UserStudyLikeService userStudyLikeService;
    private final ReviewPostService reviewPostService;

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

        // 탈퇴한 회원인지 확인
        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        verifyUserPassword(request.password(), user.getPassword(), ErrorCode.AUTHENTICATION_FAILED);

        return user.getId();
    }

    public void withdraw(WithdrawRequestDto request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        verifyUserPassword(request.password(), user.getPassword(), ErrorCode.PASSWORD_NOT_MATCH);

        user.softDelete();
        userStudyLikeService.withdrawByUserId(userId);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public User updateMyPage(MyPageRequestDto request, Long userId) {
        User user = getUserById(userId);

        if (request.nickname().trim().isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_CONTENT_ERROR);
        }

        if (request.email() != null) {
            userRepository.findByEmail(request.email())
                    .ifPresent(u -> {
                        throw new CustomException(ErrorCode.EMAIL_DUPLICATE);
                    });
        }

        userRepository.findByNickname(request.nickname())
                .ifPresent(u -> {
                    throw new CustomException(ErrorCode.NICKNAME_DUPLICATE);
                });

        return user.update(request);
    }

    private void verifyUserPassword(String plainPassword, String hashPassword, ErrorCode errorCode) {
        boolean matchedBcrypt = bcryptService.matchBcrypt(plainPassword, hashPassword);
        if (!matchedBcrypt) {
            throw new CustomException(errorCode);
        }
    }

    public UserStudiesResponseDto getUserStudies(Long userId, Long cursor) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<UserStudyLink> studyLinks = userStudyLikeService.getAllParticipatingStudy(userId, cursor);

        return UserStudiesResponseDto.of(user, studyLinks);
    }

    public UserReviewsResponseDto getUserReviews(Long userId, Long cursor) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<ReviewPost> reviews = reviewPostService.getAllReviews(userId, cursor);

        return UserReviewsResponseDto.of(user, reviews);
    }

    public List<Long> getAllId() {
        return userRepository.getAllId();
    }
}
