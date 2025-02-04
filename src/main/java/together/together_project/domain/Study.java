package together.together_project.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.dto.request.StudyPostUpdateRequestDto;
import together.together_project.validator.StudyValidator;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Study extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long studyId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User leader;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "study_post_id")
    private StudyPost studyPost;

    private String location;

    private int participantCount;

    private int maxPeople;

    public Study update(StudyPostUpdateRequestDto request) {
        if (request.title() != null) {
            if (request.title().trim().isBlank()) {
                throw new CustomException(ErrorCode.EMPTY_CONTENT_ERROR);
            }

            studyPost.updateTitle(request.title());
        }

        if (request.content() != null) {
            if (request.content().trim().isBlank()) {
                throw new CustomException(ErrorCode.DATA_NOT_FOUND);
            }

            studyPost.updateContent(request.content());
        }

        if (request.location() != null) {
            this.location = request.location();
        }

        if (request.maxPeople() != null) {
            StudyValidator.checkMaxPeopleMoreThanMinimum(request.maxPeople());
            this.maxPeople = request.maxPeople();
        }

        this.updateTime();
        studyPost.updateTime();

        return this;
    }

    public boolean isFulled() {
        return maxPeople <= participantCount;
    }

    public void increaseParticipantCount() {
        if (isFulled()) {
            throw new CustomException(ErrorCode.STUDY_IS_FULLED);
        }

        participantCount++;
    }

    public void decreaseParticipantCount() {
        if (participantCount <= 1) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        participantCount--;
    }
}
