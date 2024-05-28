package together.together_project.controller;

import java.time.LocalDateTime;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPost;

public record
StudyPostUpdateResponseDto(
        Long id,
        Long leaderId,
        String title,
        String content,
        int totalLikeCount,
        String location,
        int participantCount,
        int maxPeople,
        LocalDateTime refreshedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static StudyPostUpdateResponseDto from(Study study) {
        StudyPost studyPost = study.getStudyPost();

        return new StudyPostUpdateResponseDto(
                studyPost.getId(),
                study.getLeader().getId(),
                studyPost.getTitle(),
                studyPost.getContent(),
                studyPost.getTotalLikeCount(),
                study.getLocation(),
                study.getParticipantCount(),
                study.getMaxPeople(),
                studyPost.getRefreshedAt(),
                study.getCreatedAt(),
                study.getUpdatedAt(),
                study.getDeletedAt()
        );
    }
}
