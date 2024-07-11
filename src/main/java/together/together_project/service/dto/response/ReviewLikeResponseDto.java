package together.together_project.service.dto.response;

import together.together_project.domain.ReviewLikeLink;

public record ReviewLikeResponseDto(
        Long id,
        Long userId,
        boolean hasLike
) {
    public static ReviewLikeResponseDto of(ReviewLikeLink reviewLikeLink, boolean hasLike) {
        return new ReviewLikeResponseDto(
                reviewLikeLink.getId(),
                reviewLikeLink.getUser().getId(),
                hasLike
        );
    }
}
