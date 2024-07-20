package together.together_project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.ReviewComment;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.dto.request.RespondToJoinRequestDto;
import together.together_project.service.dto.request.ReviewCommentCreateRequestDto;
import together.together_project.service.dto.request.ReviewCommentUpdatedRequestDto;
import together.together_project.service.dto.request.ReviewCreateRequestDto;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;

@SpringBootTest
@Transactional
class ReviewCommentServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private StudyService studyService;

    @Autowired
    private UserStudyLikeService userStudyLikeService;

    @Autowired
    private ReviewPostService reviewPostService;

    @Autowired
    private ReviewCommentService reviewCommentService;

    @BeforeEach
    public void setup() {
        createUser("aaa@google.com", "aaa");
        User leader = getLeader();
        StudyPostCreateRequestDto studyPostCreateRequest = new StudyPostCreateRequestDto("title", "content", "location",
                5);
        Study study = studyService.createStudyPost(studyPostCreateRequest, leader);

        createUser("bbb@google.com", "bbb");
        User user = getUser(1);
        userStudyLikeService.join(study.getStudyId(), user);

        RespondToJoinRequestDto respondToJoinRequest = new RespondToJoinRequestDto(user.getId(), true);
        userStudyLikeService.respondToJoinRequest(respondToJoinRequest, study.getStudyId(), leader);

        ReviewPost review = reviewPostService.write(new ReviewCreateRequestDto(study.getStudyId(), "review", "url"),
                user);
        reviewCommentService.writeComment(review.getId(), new ReviewCommentCreateRequestDto("review content"), user);
    }


    @Nested
    class WriteComment {

        @DisplayName("Review 댓글 작성 가능")
        @Test
        public void writeCommentTest() {
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            User user = getUser(1);
            ReviewComment comment = reviewCommentService.writeComment(review.getId(),
                    new ReviewCommentCreateRequestDto("review content"), user);

            assertThat(comment.getAuthor()).isEqualTo(user);
            assertThat(comment.getContent()).isEqualTo("review content");
            assertThat(comment.getParentCommentId()).isNull();
            assertThat(comment.getReviewPost()).isEqualTo(review);
        }

        @DisplayName("리뷰가 존재하지 않는 경우 예외 발생")
        @Test
        void writeCommentTest2() {
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            User user = getUser(1);
            reviewPostService.withdrawReview(review.getId(), user);

            assertThatThrownBy(() -> reviewCommentService.writeComment(review.getId(),
                    new ReviewCommentCreateRequestDto("review content"), user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.REVIEW_NOT_FOUND.getDescription());
        }
    }

    /* NOTE - 리뷰 댓글 수정
         작성자가 아닌 경우
         리뷰가 존재하지 않는 경우
         내용을 입력하지 않는 경우
         댓글이 존재하지 않는 경우
     */

    @Nested
    class UpdateComment {

        @DisplayName("리뷰 댓글 수정")
        @Test
        void updateCommentTest() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment comment = reviewCommentService.getAllComment(review.getId(), null).get(0);

            ReviewComment updatedComment = reviewCommentService.updateComment(
                    review.getId(),
                    comment.getId(),
                    new ReviewCommentUpdatedRequestDto("update review comment"),
                    user);

            assertThat(updatedComment.getAuthor()).isEqualTo(user);
            assertThat(updatedComment.getReviewPost()).isEqualTo(review);
            assertThat(updatedComment.getContent()).isEqualTo("update review comment");
            assertThat(updatedComment.getParentCommentId()).isNull();
        }

        @DisplayName("작성자가 아닌 경우 예외 발생")
        @Test
        void updateCommentTest2() {
            User leader = getLeader();
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment comment = reviewCommentService.getAllComment(review.getId(), null).get(0);

            assertThatThrownBy(() -> reviewCommentService.updateComment(
                    review.getId(),
                    comment.getId(),
                    new ReviewCommentUpdatedRequestDto("update review comment"),
                    leader))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("댓글이 존재하지 않는 경우 예외 발생")
        @Test
        void updateCommentTest3() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment comment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            reviewCommentService.withdrawComment(review.getId(), comment.getId(), user);

            assertThatThrownBy(() -> reviewCommentService.updateComment(
                    review.getId(),
                    comment.getId(),
                    new ReviewCommentUpdatedRequestDto("update review comment"),
                    user))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }
    }

    /* NOTE - 리뷰 대댓글 작성
        리뷰가 존재하지 않는 경우
        부모 댓글이 존재하지  않는 경우
        내용을 입력하지 않는 경우
        parentCommentId 가 parentComment가 아닌 경우
     */

    @Nested
    class WriteChildComment {

    }

    /* NOTE - 리뷰 대댓글 수정
        작성자가 아닌 경우
        리뷰가 존재하지 않는 경우
        부모 댓글이 존재하지 않는 경우
        대댓글이 존재하지 않는 경우
        내용이 존재하지 않는 경우
        childCommentId 가 childComment가 아닌 경우
     */

    /* NOTE - 리뷰 대댓글 삭제
         작성자가 아닌 경우
         리뷰가 존재하지 않는 경우
         부모 댓글이 존재하지 않는 경우
         대댓글이 존재하지 않는 경우
         childCommentId 가 childComment가 아닌 경우
     */

    /* NOTE - 댓글 전체 조회
        리뷰가 존재하지 않는 경우
        데이터가 없는 경우
     */

    /* NOTE - 대댓글 전체 조회
        스터디가 존재하지 않는 경우
        부모 댓글이 삭제된 경우
        데이터가 없는 경우
        parentCommentId가 parentComment가 아닌 경우
     */

    private Study getStudy(int index) {
        return studyService.getAllStudy(null).get(index);
    }

    private void createUser(String email, String nickname) {
        SignupRequestDto request = new SignupRequestDto(email, nickname, "a12345678!", null, null);
        userService.signup(request);
    }

    private User getLeader() {
        Long userId = userService.getAllId().get(0);
        return userService.getUserById(userId);
    }

    private User getUser(int index) {
        Long userId = userService.getAllId().get(index);
        return userService.getUserById(userId);
    }
}