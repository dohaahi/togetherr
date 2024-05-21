package together.together_project.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.service.dto.request.LoginRequestDto;
import together.together_project.service.dto.request.MyPageRequestDto;
import together.together_project.service.dto.request.SignupRequestDto;

@SpringBootTest
@Transactional
class UserControllerTest {

    @Autowired
    private Validator validator;

    @Nested
    class SignUp {

        @DisplayName("모든 데이터가 잘 들어온 경우 통과")
        @Test
        public void testValidSignUpDto() {
            SignupRequestDto request = new SignupRequestDto(
                    "abc@google.com",
                    "aaa",
                    "a12345678!",
                    null,
                    null
            );

            assertThat(request.email()).isEqualTo("abc@google.com");
        }

        @DisplayName("이메일 형식에 맞지 않으면 예외 발생")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"abc", "abc@", "abc@google"})
        public void testEmailPattern(String input) {
            SignupRequestDto request = new SignupRequestDto(
                    input,
                    "aaa",
                    "a12345678!",
                    null,
                    null
            );

            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(request);

            assertEquals("잘못된 이메일 양식입니다.", violations.iterator().next().getMessage());
        }

        @DisplayName("닉네임 길이가 기준에 미치지 못할 경우 예외 발생")
        @ParameterizedTest
        @NullAndEmptySource
        public void testNicknameLength(String input) {
            SignupRequestDto request = new SignupRequestDto(
                    "abc@google.com",
                    input,
                    "a12345678!",
                    null,
                    null
            );

            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(request);

            assertEquals("닉네임은 1글자 이상으로 작성해주세요.", violations.iterator().next().getMessage());
        }

        @DisplayName("비밀번호가 형식에 맞지 않으면 예외 발생")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"a", "1", "!", "a12345678", "1234567!", "a!@#$%^&*"})
        public void testPasswordPattern(String input) {
            SignupRequestDto request = new SignupRequestDto(
                    "abc@google.com",
                    "aaa",
                    input,
                    null,
                    null
            );

            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(request);

            assertEquals("영문, 숫자, 특수문자 포함한 8자리 이상으로 작성해주세요.", violations.iterator().next().getMessage());
        }
    }

    @Nested
    class login {

        @DisplayName("이메일을 입력하지 않으면 예외 발생")
        @ParameterizedTest
        @NullAndEmptySource
        public void testEmailNotBlank(String input) {
            LoginRequestDto request = new LoginRequestDto(
                    input,
                    "a123456789!"
            );

            Set<ConstraintViolation<LoginRequestDto>> validations = validator.validate(request);

            assertEquals("이메일을 입력하지 않았습니다.", validations.iterator().next().getMessage());
        }

        @DisplayName("비밀번호를 입력하지 않으면 예외 발생")
        @ParameterizedTest
        @NullAndEmptySource
        public void testPasswordNotBlank(String input) {
            LoginRequestDto request = new LoginRequestDto(
                    "aaa@google.com",
                    input
            );

            Set<ConstraintViolation<LoginRequestDto>> validations = validator.validate(request);

            assertEquals("비밀번호를 입력하지 않았습니다.", validations.iterator().next().getMessage());
        }
    }

    @Nested
    class UpdateMyPage {

        @ParameterizedTest
        @ValueSource(strings = {"abc", "abc@", "abc@google"})
        public void testEmailPattern(String input) {
            MyPageRequestDto request = new MyPageRequestDto(
                    input,
                    "aaa",
                    "a12345678!",
                    null
            );

            Set<ConstraintViolation<MyPageRequestDto>> violations = validator.validate(request);

            assertEquals("잘못된 이메일 양식입니다.", violations.iterator().next().getMessage());
        }

        @ParameterizedTest
        @EmptySource
        public void testNicknameLength(String input) {
            MyPageRequestDto request = new MyPageRequestDto(
                    "bbb@google.com",
                    input,
                    "a12345678!",
                    null
            );

            Set<ConstraintViolation<MyPageRequestDto>> violations = validator.validate(request);

            assertEquals("닉네임은 1글자 이상으로 작성해주세요.", violations.iterator().next().getMessage());
        }
    }
}