package together.together_project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static together.together_project.validator.StudyValidator.MIN_PEOPLE;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.dto.request.StudiesRequestDto;
import together.together_project.service.dto.request.WithdrawRequestDto;
import together.together_project.validator.StudyValidator;

@SpringBootTest
@Transactional
class StudyControllerTest {

    @Autowired
    StudyController studyController;

    @Autowired
    private Validator validator;

    @Nested
    class write {

        @DisplayName("모든 데이터가 잘 들어온 경우 통과")
        @Test
        public void testValidStudiesRequestDto() {
            StudiesRequestDto dto = new StudiesRequestDto(
                    "Valid Title",
                    "Valid Content",
                    "Valid Location",
                    5);
            Set<ConstraintViolation<StudiesRequestDto>> violations = validator.validate(dto);

            assertEquals(0, violations.size());
        }

        @DisplayName("title이 null인 경우 예외 발생")
        @Test
        public void testTitleNotNull() {
            StudiesRequestDto dto = new StudiesRequestDto(
                    null,
                    "Valid Content",
                    "Valid Location",
                    5);
            Set<ConstraintViolation<StudiesRequestDto>> violations = validator.validate(dto);

            assertEquals("제목을 입력하지 않았습니다.", violations.iterator().next().getMessage());
        }

        @DisplayName("content가 null인 경우 예외 발생")
        @Test
        public void testContentNotNull() {
            StudiesRequestDto dto = new StudiesRequestDto(
                    "Valid Title",
                    null,
                    "Valid Location",
                    5);
            Set<ConstraintViolation<StudiesRequestDto>> violations = validator.validate(dto);

            assertEquals("내용을 입력하지 않았습니다.", violations.iterator().next().getMessage());
        }

        @DisplayName("location이 null인 경우 예외 발생")
        @Test
        public void testLocationNotNull() {
            StudiesRequestDto dto = new StudiesRequestDto(
                    "Valid Title",
                    "Valid Content",
                    null,
                    5);
            Set<ConstraintViolation<StudiesRequestDto>> violations = validator.validate(dto);

            assertEquals("위치를 입력하지 않았습니다.", violations.iterator().next().getMessage());
        }

        @DisplayName("maxPeople이 null인 경우 예외 발생")
        @Test
        public void testMaxPeopleNotNull() {
            StudiesRequestDto dto = new StudiesRequestDto(
                    "Valid Title",
                    "Valid Content",
                    "Valid Location",
                    null);
            Set<ConstraintViolation<StudiesRequestDto>> violations = validator.validate(dto);

            assertEquals("최대 인원을 입력하지 않았습니다.", violations.iterator().next().getMessage());
        }

        @DisplayName("maxPeople이 " + MIN_PEOPLE + "미만인 경우 예외 발생")
        @Test
        public void testMaxPeopleMinValue() {
            StudiesRequestDto request = new StudiesRequestDto(
                    "Valid Title",
                    "Valid Content",
                    "Valid Location",
                    0);

            assertThrows(CustomException.class,
                    () -> StudyValidator.checkMaxPeopleMoreThanMinimum(request),
                    ErrorCode.MAX_PEOPLE_UNDER_LIMIT.getDescription());
        }
    }

    @Nested
    class Withdraw {
        @DisplayName("비밀번호를 입력하지 않으면 예외 발생")
        @ParameterizedTest
        @NullAndEmptySource
        public void testPasswordNotBlank(String input) {
            WithdrawRequestDto request = new WithdrawRequestDto(input);

            Set<ConstraintViolation<WithdrawRequestDto>> violations = validator.validate(request);

            assertEquals("비밀번호를 입력하지 않았습니다.", violations.iterator().next().getMessage());
        }
    }
}