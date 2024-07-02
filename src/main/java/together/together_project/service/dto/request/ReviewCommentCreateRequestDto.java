package together.together_project.service.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record ReviewCommentCreateRequestDto(

        @NotEmpty(message = "내용을 입력하지 않았습니다.")
        String content
) {
}
