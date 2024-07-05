package together.together_project.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStudyJoinStatus {
    LEADER("리더"),
    PENDING("대기 중"),
    APPROVED("승인"),
    REJECTED("거절");

    private final String description;
}
