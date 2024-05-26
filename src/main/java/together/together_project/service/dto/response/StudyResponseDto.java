package together.together_project.service.dto.response;

import java.time.LocalDateTime;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPost;

public record StudyResponseDto(
        Long id,
        Long leader,
        String title,
        String content,
        int totalLikeCount,
        String location,
        int participantCount,
        int maxPeople,
        boolean isFulled,
        LocalDateTime refreshedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {

    public static StudyResponseDto from(Study study) {
        StudyPost studyPost = study.getStudyPost();

        return new StudyResponseDto(
                studyPost.getId(),
                study.getLeader().getId(),
                studyPost.getTitle(),
                studyPost.getContent(),
                studyPost.getTotalLikeCount(),
                study.getLocation(),
                study.getParticipantCount(),
                study.getMaxPeople(),
                study.isFulled(),
                studyPost.getRefreshedAt(),
                studyPost.getCreatedAt(),
                studyPost.getUpdatedAt(),
                studyPost.getDeletedAt()
        );
    }
}
