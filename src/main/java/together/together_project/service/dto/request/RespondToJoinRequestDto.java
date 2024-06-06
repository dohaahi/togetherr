package together.together_project.service.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RespondToJoinRequestDto(

        @NotNull(message = "잘못된 요청건입니다.")
        Long userId,

        @NotNull(message = "잘못된 요청건입니다.")
        boolean response
) {
}
