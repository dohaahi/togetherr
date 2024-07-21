package together.together_project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

            ReviewComment updatedComment = reviewCommentService.updateComment(review.getId(), comment.getId(),
                    new ReviewCommentUpdatedRequestDto("update review comment"), user);

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

            assertThatThrownBy(() -> reviewCommentService.updateComment(review.getId(), comment.getId(),
                    new ReviewCommentUpdatedRequestDto("update review comment"), leader)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("댓글이 존재하지 않는 경우 예외 발생")
        @Test
        void updateCommentTest3() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment comment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            reviewCommentService.withdrawComment(review.getId(), comment.getId(), user);

            assertThatThrownBy(() -> reviewCommentService.updateComment(review.getId(), comment.getId(),
                    new ReviewCommentUpdatedRequestDto("update review comment"), user)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }
    }

    @Nested
    class WriteChildComment {

        @DisplayName("리뷰 대댓글 작성")
        @Test
        void writeChildCommentTest() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);

            assertThat(childComment.getParentCommentId()).isEqualTo(parentComment.getId());
            assertThat(childComment.getReviewPost()).isEqualTo(review);
            assertThat(childComment.getAuthor()).isEqualTo(user);
            assertThat(childComment.getContent()).isEqualTo("child comment");
        }

        @DisplayName("부모 댓글이 존재하지 않는 경우 예외 발생")
        @Test
        void writeChildCommentTest2() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            reviewCommentService.withdrawComment(review.getId(), parentComment.getId(), user);

            assertThatThrownBy(() -> reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        @DisplayName("리뷰가 존재하지 않는 경우 예외 발생")
        @Test
        void writeChildCommentTest3() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            reviewPostService.withdrawReview(review.getId(), user);

            assertThatThrownBy(() -> reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.REVIEW_NOT_FOUND.getDescription());
        }

        @DisplayName("parentCommentId 가 parentComment가 아닌 경우 예외 발생")
        @Test
        void writeChildCommentTest4() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);

            assertThatThrownBy(() -> reviewCommentService.writeChildComment(review.getId(), childComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
        }
    }

    @Nested
    class UpdateChildComment {

        @DisplayName("대댓글 수정 가능")
        @Test
        public void updateChildComment() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);

            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);

            ReviewCommentUpdatedRequestDto commentUpdateRequest = new ReviewCommentUpdatedRequestDto(
                    "update child content");
            reviewCommentService.updateChildComment(review.getId(), parentComment.getId(), childComment.getId(),
                    commentUpdateRequest, user);

            assertThat(childComment.getParentCommentId()).isEqualTo(parentComment.getId());
            assertThat(childComment.getReviewPost()).isEqualTo(review);
            assertThat(childComment.getAuthor()).isEqualTo(user);
            assertThat(childComment.getContent()).isEqualTo("update child content");
        }

        @DisplayName("댓글 작성자가 아닌 경우 예외 발생")
        @Test
        public void updateChildComment2() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);

            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);

            ReviewCommentUpdatedRequestDto commentUpdateRequest = new ReviewCommentUpdatedRequestDto(
                    "update child content");

            assertThatThrownBy(() -> reviewCommentService.updateChildComment(review.getId(), parentComment.getId(),
                    childComment.getId(), commentUpdateRequest, getLeader())).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("리뷰가 삭제된 경우 예외 발생")
        @Test
        public void updateChildComment3() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);
            reviewPostService.withdrawReview(review.getId(), user);

            ReviewCommentUpdatedRequestDto commentUpdateRequest = new ReviewCommentUpdatedRequestDto(
                    "update child content");

            assertThatThrownBy(() -> reviewCommentService.updateChildComment(review.getId(), parentComment.getId(),
                    childComment.getId(), commentUpdateRequest, user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.REVIEW_NOT_FOUND.getDescription());
        }

        @DisplayName("댓글이 존재하지 않는 경우 예외 발생")
        @Test
        public void updateChildComment4() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);
            reviewCommentService.withdrawChildComment(review.getId(), parentComment.getId(), childComment.getId(),
                    user);
            ReviewCommentUpdatedRequestDto commentUpdateRequest = new ReviewCommentUpdatedRequestDto(
                    "update child content");

            assertThatThrownBy(() -> reviewCommentService.updateChildComment(review.getId(), parentComment.getId(),
                    childComment.getId(), commentUpdateRequest, user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        @DisplayName("내용을 입력하지 않은 경우 예외 발생")
        @ParameterizedTest
        @ValueSource(strings = {"", "     "})
        public void updateChildComment5(String content) {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);

            ReviewCommentUpdatedRequestDto commentUpdateRequest = new ReviewCommentUpdatedRequestDto(content);

            assertThatThrownBy(() -> reviewCommentService.updateChildComment(review.getId(), parentComment.getId(),
                    childComment.getId(), commentUpdateRequest, user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.EMPTY_CONTENT_ERROR.getDescription());
        }

        @DisplayName("부모 댓글이 존재하지 않는 경우 예외 발생")
        @Test
        public void writeChildCommentComment5() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);
            reviewCommentService.withdrawComment(review.getId(), parentComment.getId(), user);
            ReviewCommentUpdatedRequestDto commentUpdateRequest = new ReviewCommentUpdatedRequestDto(
                    "update child content");

            assertThatThrownBy(() -> reviewCommentService.updateChildComment(review.getId(), parentComment.getId(),
                    childComment.getId(), commentUpdateRequest, user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        @DisplayName("childCommentId 가 childComment가 아닌 경우 예외 발생")
        @Test
        public void writeChildCommentComment6() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);

            ReviewCommentUpdatedRequestDto commentUpdateRequest = new ReviewCommentUpdatedRequestDto(
                    "update child content");

            assertThatThrownBy(() -> reviewCommentService.updateChildComment(review.getId(), childComment.getId(),
                    childComment.getId(), commentUpdateRequest, user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
        }
    }

    @Nested
    class DeleteChildComment {

        @DisplayName("리뷰 대댓글 삭제")
        @Test
        void deleteChildCommentTest() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);

            reviewCommentService.withdrawChildComment(review.getId(), parentComment.getId(), childComment.getId(),
                    user);

            assertThat(childComment.getDeletedAt()).isNotNull();
        }

        @DisplayName("리뷰 작성자가 아닌 경우 예외 발생")
        @Test
        void deleteChildCommentTest2() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);

            assertThatThrownBy(() -> reviewCommentService.withdrawChildComment(review.getId(), parentComment.getId(),
                    childComment.getId(), getLeader())).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("리뷰가 존재하지 않는 경우 예외 발생")
        @Test
        void deleteChildCommentTest3() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);
            reviewPostService.withdrawReview(review.getId(), user);

            assertThatThrownBy(() -> reviewCommentService.withdrawChildComment(review.getId(), parentComment.getId(),
                    childComment.getId(), user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.REVIEW_NOT_FOUND.getDescription());
        }

        @DisplayName("부모 댓글이 존재하지 경우 예외 발생")
        @Test
        void deleteChildCommentTest4() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);
            reviewCommentService.withdrawComment(review.getId(), parentComment.getId(), user);

            assertThatThrownBy(() -> reviewCommentService.withdrawChildComment(review.getId(), parentComment.getId(),
                    childComment.getId(), user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        @DisplayName("대댓글이 존재하지 경우 예외 발생")
        @Test
        void deleteChildCommentTest5() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);
            reviewCommentService.withdrawChildComment(review.getId(), parentComment.getId(), childComment.getId(),
                    user);

            assertThatThrownBy(() -> reviewCommentService.withdrawChildComment(review.getId(), parentComment.getId(),
                    childComment.getId(), user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        @DisplayName("childCommentId 가 childComment가 아닌 경우 예외 발생")
        @Test
        public void deleteChildCommentTest6() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), user);

            ReviewCommentUpdatedRequestDto commentUpdateRequest = new ReviewCommentUpdatedRequestDto(
                    "update child content");

            assertThatThrownBy(() -> reviewCommentService.withdrawChildComment(review.getId(), childComment.getId(),
                    childComment.getId(), user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
        }
    }

    @Nested
    class GetAllComment {

        @DisplayName("댓글 전체 조회 기능")
        @Test
        public void getAllComment() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);

            for (int i = 0; i < 10; i++) {
                reviewCommentService.writeComment(review.getId(), new ReviewCommentCreateRequestDto("content"), user);
            }

            List<ReviewComment> comments = reviewCommentService.getAllComment(review.getId(), null);

            assertThat(comments.size()).isEqualTo(11);
        }

        @DisplayName("리뷰가 존재하지 않는 경우")
        @Test
        public void getAllComment2() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);

            for (int i = 0; i < 10; i++) {
                reviewCommentService.writeComment(review.getId(), new ReviewCommentCreateRequestDto("content"), user);
            }
            reviewPostService.withdrawReview(review.getId(), user);

            assertThatThrownBy(() -> reviewCommentService.getAllComment(review.getId(), null)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.REVIEW_NOT_FOUND.getDescription());
        }


        @DisplayName("데이터가 존재하지 않는 경우")
        @Test
        public void getAllComment3() {
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            reviewCommentService.getAllComment(review.getId(), null).get(0).softDelete();

            assertThatThrownBy(() -> reviewCommentService.getAllComment(review.getId(), null)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.DATA_NOT_FOUND.getDescription());
        }
    }

    @Nested
    class GetAllChildComment {

        @DisplayName("대댓글 전체 조회 기능")
        @Test
        public void getAllComment() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);

            for (int i = 0; i < 10; i++) {
                reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                        new ReviewCommentCreateRequestDto("child comment"), user);
            }

            List<ReviewComment> comments = reviewCommentService.getAllChildComment(review.getId(),
                    parentComment.getId(), null);

            assertThat(comments.size()).isEqualTo(10);
        }

        @DisplayName("리뷰가 존재하지 않는 경우")
        @Test
        public void getAllComment2() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);

            for (int i = 0; i < 10; i++) {
                reviewCommentService.writeComment(review.getId(), new ReviewCommentCreateRequestDto("content"), user);
            }
            reviewPostService.withdrawReview(review.getId(), user);

            assertThatThrownBy(() -> reviewCommentService.getAllChildComment(review.getId(), parentComment.getId(),
                    null)).isInstanceOf(CustomException.class).hasMessage(ErrorCode.REVIEW_NOT_FOUND.getDescription());
        }


        @DisplayName("데이터가 존재하지 않는 경우")
        @Test
        public void getAllComment3() {
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);

            assertThatThrownBy(() -> reviewCommentService.getAllChildComment(review.getId(), parentComment.getId(),
                    null)).isInstanceOf(CustomException.class).hasMessage(ErrorCode.DATA_NOT_FOUND.getDescription());
        }

        @DisplayName("부모 댓글이 삭제된 경우")
        @Test
        public void getAllComment4() {
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            reviewCommentService.withdrawComment(review.getId(), parentComment.getId(), getUser(1));

            assertThatThrownBy(() -> reviewCommentService.getAllChildComment(review.getId(), parentComment.getId(),
                    null)).isInstanceOf(CustomException.class).hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        @DisplayName("parentCommentId가 parentComment가 아닌 경우")
        @Test
        public void getAllComment5() {
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            ReviewComment parentComment = reviewCommentService.getAllComment(review.getId(), null).get(0);
            ReviewComment childComment = reviewCommentService.writeChildComment(review.getId(), parentComment.getId(),
                    new ReviewCommentCreateRequestDto("child comment"), getUser(1));

            assertThatThrownBy(() -> reviewCommentService.getAllChildComment(review.getId(), childComment.getId(),
                    null)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
        }
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