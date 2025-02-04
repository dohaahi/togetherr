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
import together.together_project.service.dto.request.CommentUpdateRequestDto;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class StudyPostComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "study_post_id")
    private StudyPost studyPost;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    private String content;

    private int totalLikeCount;

    private Long parentCommentId;

    public void update(CommentUpdateRequestDto request) {
        updateTime();
        this.content = request.content();
    }

    public StudyPostComment like() {
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
