package together.together_project.service.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record WithdrawRequestDto(

        @NotBlank(message = "비밀번호를 입력하지 않았습니다.")
        String password
) {
}
