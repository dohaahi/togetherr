package together.together_project.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class UserStudyLink extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User participant;

    @Enumerated(EnumType.STRING)
    private UserStudyJoinStatus status;

    public static UserStudyLink toUserStudyLink(Study study, User user) {
        return UserStudyLink.builder()
                .study(study)
                .participant(user)
                .build();
    }

    public void pending() {
        status = UserStudyJoinStatus.PENDING;
    }

    public void approve() {
        if (status == UserStudyJoinStatus.APPROVED) {
            throw new CustomException(ErrorCode.USER_ALREADY_APPROVED);
        }

        study.increaseParticipantCount();
        status = UserStudyJoinStatus.APPROVED;
    }

    public void reject() {
        status = UserStudyJoinStatus.REJECTED;
    }
}
