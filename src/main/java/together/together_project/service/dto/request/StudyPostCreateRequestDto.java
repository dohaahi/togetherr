package together.together_project.service.dto.request;

import static together.together_project.constant.StudyConstant.INIT_PARTICIPANT_COUNT;
import static together.together_project.constant.StudyConstant.INIT_TOTAL_LIKE_COUNT;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPost;
import together.together_project.domain.User;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StudyPostCreateRequestDto(

        @NotBlank(message = "제목을 입력하지 않았습니다.")
        String title,

        @NotBlank(message = "내용을 입력하지 않았습니다.")
        String content,

        @NotBlank(message = "위치를 입력하지 않았습니다.")
        String location,

        @NotNull(message = "최대 인원을 입력하지 않았습니다.")
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
