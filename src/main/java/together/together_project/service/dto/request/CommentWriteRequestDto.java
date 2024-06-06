package together.together_project.service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentWriteRequestDto(

        @NotBlank(message = "내용을 입력하지 않았습니다.")
        String content
) {
}
