package together.together_project.service.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StudyPostBumpRequestDto(

        @NotBlank(message = "끌어올리기 날짜가 입력되지 않았습니다.")
        LocalDateTime refreshedAt
) {
}
