package together.together_project.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.dto.request.StudyPostBumpRequestDto;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class StudyPost extends BaseTimeEntity {

    public final static int REFRESHED_AT_PERIOD = 2;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long studyPostId;

    private String title;

    private String content;

    private int totalLikeCount;

    private LocalDateTime refreshedAt;

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void bumpStudyPost(StudyPostBumpRequestDto request) {
        if (refreshedAt != null) {
            if (request.refreshedAt().isBefore(refreshedAt) ||
                    refreshedAt.plusDays(REFRESHED_AT_PERIOD).isAfter(request.refreshedAt()) ||
                    refreshedAt.isEqual(request.refreshedAt())
            ) {
                throw new CustomException(ErrorCode.POST_BUMP_PERIOD_EXCEPTION);
            }
        }

        refreshedAt = request.refreshedAt();
    }

    public StudyPost like() {
        totalLikeCount++;

        return this;
    }
}
