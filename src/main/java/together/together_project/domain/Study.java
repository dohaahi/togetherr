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

    // NOTE: post를 수정하고 싶은데 study를 통해서 수정이 가능 -> 어떻게 해결...?
    // 1. 지금같은 방법
    // 2. request를 study, studyPost 두 번 전달
    public Study update(StudyPostUpdateRequestDto request) {
        if (request.title() != null) {
            studyPost.updateTitle(request.title());
        }

        if (request.content() != null) {
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
}
