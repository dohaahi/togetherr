package together.together_project.service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import together.together_project.domain.ReviewComment;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.User;

public record ReviewCommentCreateRequestDto(

        @NotEmpty(message = "내용을 입력하지 않았습니다.")
        String content
) {
    public ReviewComment toReviewComment(ReviewPost review, User user) {
        return ReviewComment.builder()
                .author(user)
                .reviewPost(review)
                .content(this.content())
                .build();
    }

    public ReviewComment toReviewComment(ReviewPost review, User user, Long commentId) {
        return ReviewComment.builder()
                .author(user)
                .reviewPost(review)
                .content(content)
                .parentCommentId(commentId)
                .build();
    }
}
