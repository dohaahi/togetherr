package together.together_project.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPostComment;
import together.together_project.domain.User;

public record CommentWriteRequestDto(

        @NotBlank(message = "내용을 입력하지 않았습니다.")
        String content
) {
    public StudyPostComment toStudyPostComment(Study study, User user) {
        return StudyPostComment.builder()
                .studyPost(study.getStudyPost())
                .author(user)
                .content(this.content())
                .build();
    }

    public StudyPostComment toStudyPostComment(Study study, User user, Long commentId) {
        return StudyPostComment.builder()
                .studyPost(study.getStudyPost())
                .author(user)
                .content(this.content())
                .parentCommentId(commentId)
                .build();
    }
}
