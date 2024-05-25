package together.together_project.service.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TokenDto(
        String accessToken
) {
    public static TokenDto from(String accessToken) {
        return new TokenDto(
                accessToken
        );
    }
}
