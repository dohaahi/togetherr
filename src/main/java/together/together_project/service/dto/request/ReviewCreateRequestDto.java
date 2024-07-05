package together.together_project.service.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.Study;
import together.together_project.domain.User;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewCreateRequestDto(

        @NotNull(message = "스터디를 선택하지 않았습니다.")
        Long studyId,

        @NotBlank(message = "내용을 입력하지 않았습니다.")
        String content,

        @NotBlank(message = "사진을 삽입하지 않았습니다.")
        String reviewPicUrl
) {
    public ReviewPost toReviewPost(Study study, User user) {
        return ReviewPost.builder()
                .author(user)
                .study(study)
                .content(this.content())
                .reviewPicUrl(this.reviewPicUrl())
                .build();
    }
}
