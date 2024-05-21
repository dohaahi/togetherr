package together.together_project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.UserJpaRepository;
import together.together_project.service.dto.request.LoginRequestDto;
import together.together_project.service.dto.request.MyPageRequestDto;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.WithdrawRequestDto;

@SpringBootTest
@Transactional
class UserServiceTest {

    // TODO: mocking test 작성하기

    @Autowired
    private UserService userService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private BcryptService bcryptService;

    @Autowired
    private ValidatorFactory validatorFactory;

    @BeforeEach
    public void setup() {
        String encodeBcrypt = bcryptService.encodeBcrypt("a12345678!");

        userJpaRepository.save(User.builder()
                .id(1L)
                .email("aaa@google.com")
                .nickname("aaa")
                .password(encodeBcrypt)
                .profileUrl(null)
                .bio(null)
                .build());
    }

    @AfterEach
    public void tearDown() {
        userJpaRepository.deleteAll();
    }

    @Nested
    class Signup {

        @DisplayName("모든 데이터가 잘 들어온 경우 통과")
        @Test
        public void testSignup() {
            SignupRequestDto request = new SignupRequestDto(
                    "bbb@google.com",
                    "bbb",
                    "a12345678!",
                    null,
                    null
            );

            userService.signup(request);
            Set<ConstraintViolation<SignupRequestDto>> violations = validatorFactory.getValidator().validate(
                    request);

            assertEquals(0, violations.size());
        }

        @DisplayName("이메일이 중복이면 예외 발생")
        @Test
        public void testEmailNotDuplicate() {
            SignupRequestDto request = new SignupRequestDto(
                    "aaa@google.com",
                    "bbb",
                    "a12345678!",
                    null,
                    null
            );

            assertThrows(CustomException.class,
                    () -> userService.signup(request),
                    ErrorCode.EMAIL_DUPLICATE.getDescription());
        }

        @DisplayName("닉네임이 중복이면 예외 발생")
        @Test
        public void testSignupNicknameNotDuplicate() {
            SignupRequestDto request = new SignupRequestDto(
                    "bbb@google.com",
                    "aaa",
                    "a12345678!",
                    null,
                    null
            );

            assertThrows(CustomException.class,
                    () -> userService.signup(request),
                    ErrorCode.NICKNAME_DUPLICATE.getDescription());
        }
    }

    @Nested
    class Login {

        @DisplayName("모든 데이터가 잘 들어온 경우 통과")
        @Test
        public void testLogin() {
            LoginRequestDto request = new LoginRequestDto(
                    "aaa@google.com",
                    "a12345678!"
            );

            userService.login(request);
            Set<ConstraintViolation<LoginRequestDto>> violations = validatorFactory.getValidator().validate(request);

            assertEquals(0, violations.size());
        }

        @DisplayName("이메일이 일치하지 않으면 예외 발생")
        @Test
        public void testEmailNotMatch() {
            LoginRequestDto request = new LoginRequestDto(
                    "aaa",
                    "a12345678!"
            );

            assertThrows(CustomException.class,
                    () -> userService.login(request),
                    ErrorCode.AUTHENTICATION_FAILED.getDescription());
        }

        @DisplayName("비밀번호가 일치하지 않으면 예외 발생")
        @Test
        public void testPasswordNotMatch() {
            LoginRequestDto request = new LoginRequestDto(
                    "aaa@google.com",
                    "aaa"
            );

            assertThrows(CustomException.class,
                    () -> userService.login(request),
                    ErrorCode.AUTHENTICATION_FAILED.getDescription());
        }
    }

    @Nested
    class Withdraw {

        @DisplayName("회원탈퇴-모든 데이터가 잘 들어온 경우 통과")
        @Test
        public void testWithdraw() {
            WithdrawRequestDto request = new WithdrawRequestDto("a12345678!");

            userService.withdraw(request, 1L);
            User deletedUser = userService.getUserById(1L);

            assertThat(deletedUser.getDeletedAt())
                    .isNotNull();
        }

        @DisplayName("비밀번호가 일치하지 않으면 예외 발생")
        @Test
        public void testWithdrawPasswordNotMatch() {
            WithdrawRequestDto request = new WithdrawRequestDto("aaa!");
            Set<ConstraintViolation<WithdrawRequestDto>> violations = validatorFactory.getValidator().validate(request);

            assertThrows(CustomException.class,
                    () -> userService.withdraw(request, 1L),
                    ErrorCode.PASSWORD_NOT_MATCH.getDescription());
        }
    }

    @Nested
    class UpdateMyPage {

        @DisplayName("회원정보수정-모든 데이터가 잘 들어온 경우 통과")
        @Test
        public void testUpdateMyPage() {
            MyPageRequestDto request = new MyPageRequestDto(
                    "bbb@google.com",
                    "bbb",
                    "a12345678!",
                    null
            );

            userService.updateMyPage(request, 1L);
            User updatedUser = userService.getUserById(1L);

            assertEquals(updatedUser.getEmail(), "bbb@google.com");
        }

        @DisplayName("이메일이 중복이면 예외 발생")
        @Test
        public void testUpdateEmailNotDuplicate() {
            MyPageRequestDto request = new MyPageRequestDto(
                    "aaa@google.com",
                    "bbb",
                    "a12345678!",
                    null
            );

            assertThrows(CustomException.class,
                    () -> userService.updateMyPage(request, 1L),
                    ErrorCode.EMAIL_DUPLICATE.getDescription());
        }

        @DisplayName("닉네임이 중복이면 예외 발생")
        @Test
        public void testNicknameNotDuplicate() {
            MyPageRequestDto request = new MyPageRequestDto(
                    "bbb@google.com",
                    "aaa",
                    "a12345678!",
                    null
            );

            assertThrows(CustomException.class,
                    () -> userService.updateMyPage(request, 1L),
                    ErrorCode.NICKNAME_DUPLICATE.getDescription());
        }
    }
}
