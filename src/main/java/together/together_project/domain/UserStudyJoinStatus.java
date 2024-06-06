package together.together_project.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStudyJoinStatus {
    PENDING("대기 중"),
    APPROVED("승인"),
    REJECTED("거절");

    private final String description;
}
