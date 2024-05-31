package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPost;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StudyPostsResponseDto(
        Long id,
        String title,
        String location,
        int participantCount,
        int maxPeople,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static StudyPostsResponseDto from(Study study) {
        StudyPost studyPost = study.getStudyPost();

        return new StudyPostsResponseDto(
                studyPost.getStudyPostId(),
                studyPost.getTitle(),
                study.getLocation(),
                study.getParticipantCount(),
                study.getMaxPeople(),
                study.getCreatedAt(),
                study.getUpdatedAt(),
                study.getDeletedAt()
        );
    }
}
