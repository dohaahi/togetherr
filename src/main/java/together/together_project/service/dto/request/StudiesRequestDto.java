package together.together_project.service.dto.request;

import static together.together_project.constant.ServiceConstant.INIT_PARTICIPANT_COUNT;
import static together.together_project.constant.ServiceConstant.INIT_TOTAL_LIKE_COUNT;

import jakarta.validation.constraints.NotNull;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPost;
import together.together_project.domain.User;

public record StudiesRequestDto(

        @NotNull(message = "제목을 입력하지 않았습니다.")
        String title,

        @NotNull(message = "내용을 입력하지 않았습니다.")
        String content,

        @NotNull(message = "위치를 입력하지 않았습니다.")
        String location,

        @NotNull(message = "최대 인원을 입력하지 않았습니다.")
        // TODO: 2명 이하일 경우 예외 처리
        Integer maxPeople

) {
    public StudyPost toStudyPost() {
        return StudyPost.builder()
                .title(title)
                .content(content)
                .totalLikeCount(INIT_TOTAL_LIKE_COUNT)
                .refreshedAt(null)
                .build();
    }

    public Study toStudy(User user, StudyPost studyPost) {
        return Study.builder()
                .leader(user)
                .studyPost(studyPost)
                .location(location)
                .participantCount(INIT_PARTICIPANT_COUNT)
                .maxPeople(maxPeople)
                .build();
    }
}
