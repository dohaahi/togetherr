package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.User;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserReviewsResponseDto(
        Long userId,
        String email,
        String nickname,
        String bio,
        String profileUrl,
        List<MetaReview> reviews
) {
    public static UserReviewsResponseDto of(User user, List<ReviewPost> review) {
        return new UserReviewsResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getBio(),
                user.getProfileUrl(),
                review.stream()
                        .map(MetaReview::of)
                        .toList()
        );
    }


    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record MetaReview(
            Long reviewId,
            String reviewPicUrl
    ) {
        public static MetaReview of(ReviewPost review) {
            return new MetaReview(
                    review.getId(),
                    review.getReviewPicUrl()
            );
        }
    }
}
