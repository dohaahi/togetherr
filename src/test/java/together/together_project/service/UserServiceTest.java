package together.together_project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.dto.request.LoginRequestDto;
import together.together_project.service.dto.request.MyPageRequestDto;
import together.together_project.service.dto.request.ReviewCreateRequestDto;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;
import together.together_project.service.dto.request.WithdrawRequestDto;
import together.together_project.service.dto.response.SignupResponseDto;
import together.together_project.service.dto.response.UserReviewsResponseDto;
import together.together_project.service.dto.response.UserStudiesResponseDto;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private StudyService studyService;

    @Autowired
    private ReviewPostService reviewPostService;


    @BeforeEach
    public void setup() {
        SignupRequestDto signupRequest = new SignupRequestDto("abc@google.com", "abc", "a123456789!", "bio",
                "profile url");

        userService.signup(signupRequest);
    }

    @Nested
    class Signup {

        @DisplayName("모든 데이터가 잘 들어온 경우 통과")
        @Test
        public void testSignup() {
            SignupRequestDto request = new SignupRequestDto("bbb@google.com", "bbb", "a12345678!", null, null);

            LocalDateTime now = LocalDateTime.now();
            SignupResponseDto signupResponse = userService.signup(request);

            assertThat(signupResponse).isNotNull();
            assertThat(signupResponse.createdAt()).isAfter(now);
            assertThat(signupResponse.updatedAt()).isAfter(now);
            assertThat(signupResponse.deletedAt()).isNull();
        }

        @DisplayName("이메일이 중복이면 예외 발생")
        @Test
        public void testEmailNotDuplicate() {
            SignupRequestDto request = new SignupRequestDto("abc@google.com", "bbb", "a12345678!", null, null);

            assertThrows(CustomException.class, () -> userService.signup(request),
                    ErrorCode.EMAIL_DUPLICATE.getDescription());
        }

        @DisplayName("닉네임이 중복이면 예외 발생")
        @Test
        public void testSignupNicknameNotDuplicate() {
            SignupRequestDto request = new SignupRequestDto("bbb@google.com", "abc", "a12345678!", null, null);

            assertThrows(CustomException.class, () -> userService.signup(request),
                    ErrorCode.NICKNAME_DUPLICATE.getDescription());
        }
    }

    @Nested
    class Login {

        @DisplayName("모든 데이터가 잘 들어온 경우 통과")
        @Test
        public void testLogin() {
            LoginRequestDto request = new LoginRequestDto("abc@google.com", "a123456789!");

            Long userId = userService.login(request);
            User user = userService.getUserById(userId);

            assertThat(user.getEmail()).isEqualTo(request.email());
            assertThat(user.getNickname()).isEqualTo("abc");
            assertThat(user.getBio()).isEqualTo("bio");
            assertThat(user.getProfileUrl()).isEqualTo("profile url");
        }

        @DisplayName("이메일이 일치하지 않으면 예외 발생")
        @Test
        public void testEmailNotMatch() {
            LoginRequestDto request = new LoginRequestDto("aaa", "a12345678!");

            assertThrows(CustomException.class, () -> userService.login(request),
                    ErrorCode.AUTHENTICATION_FAILED.getDescription());
        }

        @DisplayName("비밀번호가 일치하지 않으면 예외 발생")
        @Test
        public void testPasswordNotMatch() {
            LoginRequestDto request = new LoginRequestDto("aaa@google.com", "aaa");

            assertThrows(CustomException.class, () -> userService.login(request),
                    ErrorCode.AUTHENTICATION_FAILED.getDescription());
        }
    }

    @Nested
    class Withdraw {

        @DisplayName("회원탈퇴-모든 데이터가 잘 들어온 경우 통과")
        @Test
        public void testWithdraw() {
            WithdrawRequestDto request = new WithdrawRequestDto("a123456789!");

            Long userId = userService.getAllId().get(0);

            userService.withdraw(request, userId);
        }

        @DisplayName("비밀번호가 일치하지 않으면 예외 발생")
        @Test
        public void testWithdrawPasswordNotMatch() {
            WithdrawRequestDto request = new WithdrawRequestDto("aaa!");

            assertThrows(CustomException.class, () -> userService.withdraw(request, 1L),
                    ErrorCode.PASSWORD_NOT_MATCH.getDescription());
        }

        @DisplayName("이미 탈퇴한 유저이면 예외 발생")
        @Test
        void testWithdraw2() {
            WithdrawRequestDto request = new WithdrawRequestDto("a123456789!");
            Long userId = userService.getAllId().get(0);
            userService.withdraw(request, userId);

            assertThrows(CustomException.class, () -> userService.withdraw(request, userId),
                    ErrorCode.USER_NOT_FOUND.getDescription());
        }
    }

    @Nested
    class UpdateMyPage {

        @DisplayName("회원정보수정-모든 데이터가 잘 들어온 경우 통과")
        @Test
        public void testUpdateMyPage() {
            MyPageRequestDto request = new MyPageRequestDto("bbb@google.com", "bbb", "bio update",
                    "profile url update");

            Long userId = userService.getAllId().get(0);
            User user = userService.updateMyPage(request, userId);
            User updatedUser = userService.getUserById(user.getId());

            assertEquals(updatedUser.getEmail(), "bbb@google.com");
            assertEquals(updatedUser.getNickname(), "bbb");
            assertEquals(updatedUser.getBio(), "bio update");
            assertEquals(updatedUser.getProfileUrl(), "profile url update");
        }

        @DisplayName("이메일이 중복이면 예외 발생")
        @Test
        public void testUpdateEmailNotDuplicate() {
            MyPageRequestDto request = new MyPageRequestDto("aaa@google.com", "bbb", "a12345678!", null);

            assertThrows(CustomException.class, () -> userService.updateMyPage(request, 1L),
                    ErrorCode.EMAIL_DUPLICATE.getDescription());
        }

        @DisplayName("닉네임이 중복이면 예외 발생")
        @Test
        public void testNicknameNotDuplicate() {
            MyPageRequestDto request = new MyPageRequestDto("bbb@google.com", "aaa", "a12345678!", null);

            assertThrows(CustomException.class, () -> userService.updateMyPage(request, 1L),
                    ErrorCode.NICKNAME_DUPLICATE.getDescription());
        }

        @DisplayName("이메일 양식에 안맞으면 예외 발생")
        @ParameterizedTest
        @ValueSource(strings = {"a", "abc", "abc.com"})
        void testSignupEmail(String email) {
            MyPageRequestDto request = new MyPageRequestDto(email, "aaa", null, null);

            assertThrows(CustomException.class, () -> userService.updateMyPage(request, 1L),
                    ErrorCode.EMAIL_FORMAT_INVALID.getDescription());
        }

        @DisplayName("닉네임이 한 글자 미만이며 예외 발생")
        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        void testSignupNickname(String nickname) {
            MyPageRequestDto request = new MyPageRequestDto("bbb@google.com", nickname, null, null);

            assertThrows(CustomException.class, () -> userService.updateMyPage(request, 1L),
                    ErrorCode.NICKNAME_LENGTH.getDescription());
        }
    }

    @Nested
    class GetUserStudies {

        @DisplayName("회원 스터디 상세 조회")
        @Test
        void testUserStudies() {
            Long userId = userService.getAllId().get(0);
            User user = userService.getUserById(userId);

            for (int i = 0; i < 5; i++) {
                createStudyPost("title" + i, "content" + i, user);
            }

            UserStudiesResponseDto studies = userService.getUserStudies(userId, null);

            assertThat(studies.studies().size()).isEqualTo(5);
            assertThat(studies.userId()).isEqualTo(user.getId());
        }

        @DisplayName("참여한 스터디가 존재하지 않는 경우 예외 발생")
        @Test
        void testUserStudies2() {
            Long userId = userService.getAllId().get(0);
            userService.getUserById(userId);

            assertThrows(CustomException.class, () -> userService.getUserStudies(userId, null),
                    ErrorCode.DATA_NOT_FOUND.getDescription());
        }
    }

    @Nested
    class getUserReviews {

        @DisplayName("회원리뷰 상세 조회")
        @Test
        void testGetReviews() {
            Long userId = userService.getAllId().get(0);
            User user = userService.getUserById(userId);

            for (int i = 0; i < 5; i++) {
                createStudyPost("title" + i, "content" + i, user);
                createReviewPost((long) (i + 1), "content" + i, "url" + i, user);
            }

            UserReviewsResponseDto reviews = userService.getUserReviews(user.getId(), null);

            assertThat(reviews.reviews().size()).isEqualTo(5);
            assertThat(reviews.userId()).isEqualTo(user.getId());
        }

        @DisplayName("작성한 리뷰가 없는 경우 예외 발생")
        @Test
        void testUserReviews2() {
            Long userId = userService.getAllId().get(0);
            userService.getUserById(userId);

            assertThrows(CustomException.class, () -> userService.getUserReviews(userId, null),
                    ErrorCode.DATA_NOT_FOUND.getDescription());
        }
    }

    private void createStudyPost(String title, String content, User user) {
        StudyPostCreateRequestDto studyRequest = new StudyPostCreateRequestDto("title", "content", "location", 5);
        studyService.createStudyPost(studyRequest, user);
    }

    private void createReviewPost(Long studyId, String content, String url, User user) {
        ReviewCreateRequestDto reviewCreateRequest = new ReviewCreateRequestDto(studyId, content, url);

        reviewPostService.write(reviewCreateRequest, user);
    }
}
