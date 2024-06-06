package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.UserStudyLink;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record JoinRequestsResponseDto(
        Long id,
        String nickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static JoinRequestsResponseDto from(UserStudyLink userStudyLink) {
        return new JoinRequestsResponseDto(
                userStudyLink.getId(),
                userStudyLink.getParticipant().getNickname(),
                userStudyLink.getCreatedAt(),
                userStudyLink.getUpdatedAt(),
                userStudyLink.getDeletedAt()
        );
    }
}
