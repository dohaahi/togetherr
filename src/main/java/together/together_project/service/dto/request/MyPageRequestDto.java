package together.together_project.service.dto.request;

import static together.together_project.exception.ErrorCode.EMAIL_REGEX;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MyPageRequestDto(
        @Pattern(regexp = EMAIL_REGEX, message = "잘못된 이메일 양식입니다.")
        String email,

        @Length(min = 1, message = "닉네임은 1글자 이상으로 작성해주세요.")
        String nickname,

        String bio,

        String profileUrl
) {
}
