package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.domain.UserStudyLink;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserStudiesResponseDto(
        Long userId,
        String email,
        String nickname,
        String bio,
        String profileUrl,
        List<MetaStudy> reviews
) {
    public static UserStudiesResponseDto of(User user, List<UserStudyLink> studies) {
        List<MetaStudy> metaStudies = studies.stream()
                .map(MetaStudy::of)
                .toList();

        return new UserStudiesResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getBio(),
                user.getProfileUrl(),
                metaStudies
        );
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record MetaStudy(
            Long studyId,
            String title,
            String location,
            int participantCount,
            int maxPeople,
            Boolean isFulled,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        public static MetaStudy of(UserStudyLink userStudyLink) {
            Study study = userStudyLink.getStudy();

            return new MetaStudy(
                    study.getStudyId(),
                    study.getStudyPost().getTitle(),
                    study.getLocation(),
                    study.getParticipantCount(),
                    study.getMaxPeople(),
                    study.isFulled(),
                    study.getCreatedAt(),
                    study.getUpdatedAt(),
                    study.getDeletedAt()
            );
        }
    }
}
