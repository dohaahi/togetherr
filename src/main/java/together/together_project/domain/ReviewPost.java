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
import together.together_project.service.dto.request.ReviewUpdateRequestDto;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    private String content;

    private String reviewPicUrl;

    private int totalLikeCount;

    public ReviewPost update(ReviewUpdateRequestDto request, Study study) {
        if (study != null) {
            updateTime();
            this.study = study;
        }

        if (request.content() != null) {
            if (request.content().trim().isBlank()) {
                throw new CustomException(ErrorCode.EMPTY_CONTENT_ERROR);
            }

            updateTime();
            content = request.content();
        }

        if (request.reviewPicUrl() != null) {
            if (request.reviewPicUrl().trim().isBlank()) {
                throw new CustomException(ErrorCode.EMPTY_CONTENT_ERROR);
            }

            updateTime();
            reviewPicUrl = request.reviewPicUrl();
        }

        return this;
    }

    public ReviewPost like() {
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
