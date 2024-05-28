package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.Study;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StudyPostBumpResponseDto(
        LocalDateTime refreshedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static StudyPostBumpResponseDto from(Study study) {
        return new StudyPostBumpResponseDto(
                study.getStudyPost().getRefreshedAt(),
                study.getCreatedAt(),
                study.getUpdatedAt(),
                study.getDeletedAt()
        );
    }
}
