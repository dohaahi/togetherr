package together.together_project.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.dto.request.ReviewCommentUpdatedRequestDto;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_post_id")
    private ReviewPost reviewPost;

    private String content;

    private Long parentCommentId;

    private int totalLikeCount;

    public ReviewComment update(ReviewCommentUpdatedRequestDto request) {
        if (request.content() != null) {
            updateTime();
            content = request.content();
        }

        return this;
    }

    public ReviewComment like() {
        totalLikeCount++;

        return this;
    }

    public void unlike() {
        if (totalLikeCount == 0) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR);
        }

        totalLikeCount--;
    }
}
