package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPost;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StudyPostCreateResponseDto(
        Long id,
        Long leaderId,
        String title,
        String content,
        int totalLikeCount,
        String location,
        int participantCount,
        int maxPeople,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {

    public static StudyPostCreateResponseDto from(Study study) {
        StudyPost studyPost = study.getStudyPost();

        return new StudyPostCreateResponseDto(
                study.getId(),
                study.getLeader().getId(),
                studyPost.getTitle(),
                studyPost.getContent(),
                studyPost.getTotalLikeCount(),
                study.getLocation(),
                study.getParticipantCount(),
                study.getMaxPeople(),
                study.getCreatedAt(),
                study.getUpdatedAt(),
                study.getDeletedAt()
        );
    }
}
