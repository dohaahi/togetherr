package together.together_project.service.dto.response;

import together.together_project.domain.ReviewCommentLikeLink;

public record ReviewCommentLikesResponseDto(
        Long id,
        String nickname,
        String profileUrl
) {
    public static ReviewCommentLikesResponseDto of(ReviewCommentLikeLink commentLike) {
        return new ReviewCommentLikesResponseDto(
                commentLike.getId(),
                commentLike.getUser().getNickname(),
                commentLike.getUser().getProfileUrl()
        );
    }
}
