package together.together_project.service.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RespondToJoinRequestDto(

        @NotNull(message = "회원 아이디가 제출되지 않았습니다.")
        Long userId,

        @NotNull(message = "응답이 제출되지 않았습니다.")
        boolean isAccept
) {
}
