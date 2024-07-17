package together.together_project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import together.together_project.domain.Study;
import together.together_project.domain.StudyPostComment;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.dto.request.CommentUpdateRequestDto;
import together.together_project.service.dto.request.CommentWriteRequestDto;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;

@SpringBootTest
@Transactional
class StudyCommentServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private StudyService studyService;

    @Autowired
    private StudyCommentService studyCommentService;

    @BeforeEach
    public void setup() {
        User leader = createUser("leader@google.com", "leader");
        createStudyPost(leader);

        for (int i = 0; i < 5; i++) {
            createUser("user" + i + "@google.com", "user" + i);
        }
    }

    @Nested
    class WriteComment {

        @DisplayName("댓글 작성 가능")
        @Test
        public void writeComment() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostComment comment = createComment("content", study, user);

            assertThat(comment.getParentCommentId()).isNull();
            assertThat(comment.getAuthor()).isEqualTo(user);
            assertThat(comment.getContent()).isEqualTo("content");
            assertThat(comment.getStudyPost()).isEqualTo(study.getStudyPost());
            assertThat(comment.getTotalLikeCount()).isEqualTo(0);
        }

        @DisplayName("유효하지 않은 스터디인 경우 예외 발생")
        @Test
        void writeComment2() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);
            studyService.deleteStudy(study.getStudyId(), getLeader());

            assertThrows(CustomException.class, () -> createComment("content", study, user),
                    ErrorCode.STUDY_NOT_FOUND.getDescription());
        }

//        @DisplayName("내용을 입력하지 않은 경우 예외 발생")
//        @Test
//        void writeComment3() {
//            User user = getUser(0);
//            Study study = studyService.getAllStudy(null).get(0);
//
//            assertThrows(CustomException.class,
//                    () -> createComment(null, study, user),
//                    ErrorCode.EMPTY_CONTENT_ERROR.getDescription());
//
//        }
    }


    /* NOTE
        스터디가 삭제된 경우
        댓글이 존재하지 않는 경우
        내용을 입력하지 않은 경우
     */

    @Nested
    class UpdateComment {

        @DisplayName("댓글 수정 가능")
        @Test
        public void updateComment() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostComment comment = createComment("content", study, user);

            CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto("update content");
            studyCommentService.updateComment(study.getStudyId(), comment.getId(), commentUpdateRequest, user);

            assertThat(comment.getParentCommentId()).isNull();
            assertThat(comment.getAuthor()).isEqualTo(user);
            assertThat(comment.getContent()).isEqualTo("update content");
            assertThat(comment.getStudyPost()).isEqualTo(study.getStudyPost());
            assertThat(comment.getTotalLikeCount()).isEqualTo(0);
        }

        @DisplayName("댓글 작성자가 아닌 경우 예외 발생")
        @Test
        public void updateComment2() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostComment comment = createComment("content", study, user);

            CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto("update content");

            assertThrows(CustomException.class,
                    () -> studyCommentService.updateComment(study.getStudyId(), comment.getId(), commentUpdateRequest,
                            getUser(1)), ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("스터디가 삭제된 경우 예외 발생")
        @Test
        public void updateComment3() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostComment comment = createComment("content", study, user);
            studyService.deleteStudy(study.getStudyId(), user);

            CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto("update content");

            assertThrows(CustomException.class,
                    () -> studyCommentService.updateComment(study.getStudyId(), comment.getId(), commentUpdateRequest,
                            getUser(1)), ErrorCode.STUDY_NOT_FOUND.getDescription());
        }

        @DisplayName("댓글이 존재하지 않는 경우 예외 발생")
        @Test
        public void updateComment4() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostComment comment = createComment("content", study, user);
            studyCommentService.withdrawComment(study.getStudyId(), comment.getId(), user);

            CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto("update content");

            assertThrows(CustomException.class,
                    () -> studyCommentService.updateComment(study.getStudyId(), comment.getId(), commentUpdateRequest,
                            getUser(1)), ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        @DisplayName("내용을 입력하지 않은 경우 예외 발생")
        @ParameterizedTest
        @ValueSource(strings = {"", "     "})
        public void updateComment5(String content) {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostComment comment = createComment("content", study, user);
            studyService.deleteStudy(study.getStudyId(), user);

            CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto(content);

            assertThrows(CustomException.class,
                    () -> studyCommentService.updateComment(study.getStudyId(), comment.getId(), commentUpdateRequest,
                            getUser(1)), ErrorCode.EMPTY_CONTENT_ERROR.getDescription());
        }
    }

    @Nested
    class DeleteComment {

        @DisplayName("댓글 삭제 가능")
        @Test
        public void deleteComment() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("new content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);

            studyCommentService.withdrawComment(study.getStudyId(), comment.getId(), user);

            assertThat(comment.getDeletedAt()).isNotNull();
        }

        @DisplayName("댓글 작성자가 아닌 경우 예외 발생")
        @Test
        public void deleteComment2() {
            User user = getUser(0);
            User otherUser = getUser(1);
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("new content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);

            assertThatThrownBy(() -> studyCommentService.withdrawComment(study.getStudyId(), comment.getId(),
                    otherUser)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("유효하지 않은 스터디인 경우 예외 발생")
        @Test
        public void deleteComment3() {
            User user = getLeader();
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("new content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            studyService.deleteStudy(study.getStudyId(), user);

            assertThatThrownBy(
                    () -> studyCommentService.withdrawComment(study.getStudyId(), comment.getId(), user)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.STUDY_NOT_FOUND.getDescription());
        }

        @DisplayName("댓글이 존재하지 경우 예외 발생")
        @Test
        public void deleteComment4() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("new content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            studyCommentService.withdrawComment(study.getStudyId(), comment.getId(), user);

            assertThatThrownBy(
                    () -> studyCommentService.withdrawComment(study.getStudyId(), comment.getId(), user)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }
    }

    @Nested
    class WriteChildComment {

        @DisplayName("대댓글 작성 가능")
        @Test
        public void writeChildCommentComment() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);

            assertThat(childComment.getParentCommentId()).isEqualTo(comment.getId());
            assertThat(childComment.getAuthor()).isEqualTo(user);
            assertThat(childComment.getContent()).isEqualTo("child content");
            assertThat(comment.getStudyPost()).isEqualTo(study.getStudyPost());
            assertThat(childComment.getTotalLikeCount()).isEqualTo(0);
        }

        @DisplayName("스터디가 존재하지 않는 경우 예외 발생")
        @Test
        public void writeChildCommentComment2() {
            User user = getLeader();
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            studyService.deleteStudy(study.getStudyId(), user);

            assertThatThrownBy(() -> studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.STUDY_NOT_FOUND.getDescription());
        }

        @DisplayName("부모 댓글이 존재하지 않는 경우 예외 발생")
        @Test
        public void writeChildCommentComment3() {
            User user = getLeader();
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            studyCommentService.withdrawComment(study.getStudyId(), comment.getId(), user);

            assertThatThrownBy(() -> studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        //        @DisplayName("내용을 입력하지 않는 경우 예외 발생")
//        @Test
//        public void writeChildCommentComment4() {
//            User user = getLeader();
//            Study study = studyService.getAllStudy(null).get(0);
//
//            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
//            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
//            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("   ");
//
//            assertThatThrownBy(() -> studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
//                    childCommentWriteRequest, user))
//                    .isInstanceOf(CustomException.class)
//                    .hasMessage(ErrorCode.EMPTY_CONTENT_ERROR.getDescription());
//        }

        @DisplayName("parentCommentId 가 parentComment가 아닌 경우 예외 발생")
        @Test
        public void writeChildCommentComment5() {
            User user = getLeader();
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);

            assertThatThrownBy(() -> studyCommentService.writeChildComment(study.getStudyId(), childComment.getId(),
                    childCommentWriteRequest, user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
        }
    }

    @Nested
    class UpdateChildComment {

        @DisplayName("대댓글 수정 가능")
        @Test
        public void updateChildComment() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);

            CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto("update child content");
            studyCommentService.updateChildComment(study.getStudyId(), comment.getId(), childComment.getId(),
                    commentUpdateRequest, user);

            assertThat(childComment.getParentCommentId()).isEqualTo(comment.getId());
            assertThat(childComment.getAuthor()).isEqualTo(user);
            assertThat(childComment.getContent()).isEqualTo("update child content");
            assertThat(comment.getStudyPost()).isEqualTo(study.getStudyPost());
            assertThat(childComment.getTotalLikeCount()).isEqualTo(0);
        }

        @DisplayName("댓글 작성자가 아닌 경우 예외 발생")
        @Test
        public void updateChildComment2() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);

            CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto("update child content");

            assertThrows(CustomException.class,
                    () -> studyCommentService.updateChildComment(study.getStudyId(), comment.getId(),
                            childComment.getId(), commentUpdateRequest, getUser(1)),
                    ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("스터디가 삭제된 경우 예외 발생")
        @Test
        public void updateChildComment3() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);
            studyService.deleteStudy(study.getStudyId(), user);

            CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto("update child content");

            assertThrows(CustomException.class,
                    () -> studyCommentService.updateChildComment(study.getStudyId(), comment.getId(),
                            childComment.getId(), commentUpdateRequest, getUser(1)),
                    ErrorCode.STUDY_NOT_FOUND.getDescription());
        }

        @DisplayName("댓글이 존재하지 않는 경우 예외 발생")
        @Test
        public void updateChildComment4() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);
            studyCommentService.withdrawChildComment(study.getStudyId(), comment.getId(), childComment.getId(), user);

            CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto("update child content");

            assertThrows(CustomException.class,
                    () -> studyCommentService.updateChildComment(study.getStudyId(), comment.getId(),
                            childComment.getId(), commentUpdateRequest, getUser(1)),
                    ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        @DisplayName("내용을 입력하지 않은 경우 예외 발생")
        @ParameterizedTest
        @ValueSource(strings = {"", "     "})
        public void updateChildComment5(String content) {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);
            studyCommentService.withdrawChildComment(study.getStudyId(), comment.getId(), childComment.getId(), user);

            CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto(content);

            assertThrows(CustomException.class,
                    () -> studyCommentService.updateChildComment(study.getStudyId(), comment.getId(),
                            childComment.getId(), commentUpdateRequest, getUser(1)),
                    ErrorCode.EMPTY_CONTENT_ERROR.getDescription());
        }

        @DisplayName("부모 댓글이 존재하지 않는 경우 예외 발생")
        @Test
        public void writeChildCommentComment5() {
            User user = getLeader();
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);
            studyCommentService.withdrawComment(study.getStudyId(), comment.getId(), user);
            CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto("update child content");

            assertThatThrownBy(() -> studyCommentService.updateChildComment(study.getStudyId(), comment.getId(),
                    childComment.getId(), commentUpdateRequest, user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        @DisplayName("childCommentId 가 childComment가 아닌 경우 예외 발생")
        @Test
        public void writeChildCommentComment6() {
            User user = getLeader();
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);
            StudyPostComment childComment2 = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);
            CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto("update child content");

            assertThatThrownBy(() -> studyCommentService.updateChildComment(study.getStudyId(), childComment.getId(),
                    childComment2.getId(), commentUpdateRequest, user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
        }
    }

    @Nested
    class WithdrawChildComment {

        @DisplayName("대댓글 삭제 가능")
        @Test
        public void withdrawChildComment() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);

            studyCommentService.withdrawChildComment(study.getStudyId(), comment.getId(), childComment.getId(), user);

            assertThat(childComment.getDeletedAt()).isNotNull();
        }

        @DisplayName("작성자가 아닌 경우 예외 발생")
        @Test
        public void withdrawChildComment2() {
            User user = getLeader();
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);

            assertThatThrownBy(() -> studyCommentService.withdrawChildComment(study.getStudyId(), comment.getId(),
                    childComment.getId(), getUser(1))).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("부모 댓글이 존재하지 않는 경우 예외 발생")
        @Test
        public void withdrawChildComment3() {
            User user = getLeader();
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);
            studyCommentService.withdrawComment(study.getStudyId(), comment.getId(), user);

            assertThatThrownBy(() -> studyCommentService.withdrawChildComment(study.getStudyId(), comment.getId(),
                    childComment.getId(), user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        @DisplayName("스터디가 존재하지 않는 경우 예외 발생")
        @Test
        public void withdrawChildComment4() {
            User user = getLeader();
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);
            studyService.deleteStudy(study.getStudyId(), user);

            assertThatThrownBy(() -> studyCommentService.withdrawChildComment(study.getStudyId(), comment.getId(),
                    childComment.getId(), user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.STUDY_NOT_FOUND.getDescription());
        }

        @DisplayName("대댓글이 존재하지 않는 경우 예외 발생")
        @Test
        public void withdrawChildComment5() {
            User user = getLeader();
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);
            studyCommentService.withdrawChildComment(study.getStudyId(), comment.getId(), childComment.getId(), user);

            assertThatThrownBy(() -> studyCommentService.withdrawChildComment(study.getStudyId(), comment.getId(),
                    childComment.getId(), user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        @DisplayName("childCommentId 가 childComment가 아닌 경우 예외 발생")
        @Test
        public void withdrawChildComment6() {
            User user = getLeader();
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
            StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
            CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
            StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);
            StudyPostComment childComment2 = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                    childCommentWriteRequest, user);

            assertThatThrownBy(() -> studyCommentService.withdrawChildComment(study.getStudyId(), childComment.getId(),
                    childComment2.getId(), user)).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
        }
    }

    @Nested
    class GetAllComment {

        @DisplayName("댓글 전체 조회 기능")
        @Test
        public void getAllComment() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            CommentWriteRequestDto commentWriteRequest1 = new CommentWriteRequestDto("content1");
            studyCommentService.write(commentWriteRequest1, study.getStudyId(), user);
            CommentWriteRequestDto commentWriteRequest2 = new CommentWriteRequestDto("content2");
            studyCommentService.write(commentWriteRequest2, study.getStudyId(), user);
            CommentWriteRequestDto commentWriteRequest3 = new CommentWriteRequestDto("content3");
            studyCommentService.write(commentWriteRequest3, study.getStudyId(), user);
            CommentWriteRequestDto commentWriteRequest4 = new CommentWriteRequestDto("content4");
            studyCommentService.write(commentWriteRequest4, study.getStudyId(), user);
            CommentWriteRequestDto commentWriteRequest5 = new CommentWriteRequestDto("content5");
            studyCommentService.write(commentWriteRequest5, study.getStudyId(), user);

            List<StudyPostComment> comments = studyCommentService.getAllComment(study.getStudyId(), null);

            assertThat(comments.size()).isEqualTo(5);
        }

        @DisplayName("스터디가 존재하지 않는 경우")
        @Test
        public void getAllComment2() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            for (int i = 0; i < 5; i++) {
                createComment("content", study, user);
            }
            studyService.deleteStudy(study.getStudyId(), user);

            assertThatThrownBy(() -> studyCommentService.getAllComment(study.getStudyId(), null)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.STUDY_NOT_FOUND.getDescription());
        }


        @DisplayName("데이터가 존재하지 않는 경우")
        @Test
        public void getAllComment3() {
            Study study = studyService.getAllStudy(null).get(0);

            assertThatThrownBy(() -> studyCommentService.getAllComment(study.getStudyId(), null)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.DATA_NOT_FOUND.getDescription());
        }
    }
    
    @Nested
    class GetAllChildComment {

        @DisplayName("대댓글 전체 조회 기능")
        @Test
        public void getAllComment() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostComment parentComment = createComment("parent comment", study, user);
            for (int i = 0; i < 10; i++) {
                createChildComment(study, user, parentComment.getId());
            }

            List<StudyPostComment> childComments = studyCommentService.getChildComment(study.getStudyId(),
                    parentComment.getId(), null);

            assertThat(childComments.size()).isEqualTo(10);
        }

        @DisplayName("스터디가 존재하지 않는 경우")
        @Test
        public void getAllComment2() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostComment parentComment = createComment("parent comment", study, user);
            for (int i = 0; i < 10; i++) {
                createChildComment(study, user, parentComment.getId());
            }
            studyService.deleteStudy(study.getStudyId(), user);

            assertThatThrownBy(() -> studyCommentService.getChildComment(study.getStudyId(), parentComment.getId(),
                    null)).isInstanceOf(CustomException.class).hasMessage(ErrorCode.STUDY_NOT_FOUND.getDescription());
        }


        @DisplayName("데이터가 존재하지 않는 경우")
        @Test
        public void getAllComment3() {
            User user = getLeader();
            Study study = studyService.getAllStudy(null).get(0);
            StudyPostComment parentComment = createComment("parent comment", study, user);

            assertThatThrownBy(() -> studyCommentService.getChildComment(study.getStudyId(), parentComment.getId(),
                    null)).isInstanceOf(CustomException.class).hasMessage(ErrorCode.DATA_NOT_FOUND.getDescription());
        }

        @DisplayName("부모 댓글이 삭제된 경우 예외 발생")
        @Test
        public void getAllComment4() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostComment parentComment = createComment("parent comment", study, user);
            for (int i = 0; i < 10; i++) {
                createChildComment(study, user, parentComment.getId());
            }
            studyCommentService.withdrawComment(study.getStudyId(), parentComment.getId(), user);

            assertThatThrownBy(() -> studyCommentService.getChildComment(study.getStudyId(), parentComment.getId(),
                    null)).hasMessage(ErrorCode.COMMENT_NOT_FOUND.getDescription());
        }

        @DisplayName("parentCommentId가 parentComment가 아닌 경우 예외 발생")
        @Test
        public void getAllComment5() {
            User user = getUser(0);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostComment parentComment = createComment("parent comment", study, user);
            StudyPostComment childComment = createChildComment(study, user, parentComment.getId());

            assertThatThrownBy(() -> studyCommentService.getChildComment(study.getStudyId(), childComment.getId(),
                    null)).hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
        }
    }

    private User createUser(String email, String nickname) {
        SignupRequestDto request = new SignupRequestDto(email, nickname, "a12345678!", null, null);
        userService.signup(request);

        return getLeader();
    }

    private void createStudyPost(User user) {
        StudyPostCreateRequestDto studyPostCreateRequest = new StudyPostCreateRequestDto("title", "content", "location",
                5);

        studyService.createStudyPost(studyPostCreateRequest, user);
    }

    private User getLeader() {
        Long userId = userService.getAllId().get(0);
        return userService.getUserById(userId);
    }

    private User getUser(int index) {
        Long userId = userService.getAllId().get(index);
        return userService.getUserById(userId);
    }

    private StudyPostComment createComment(String content, Study study, User user) {
        CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto(content);

        return studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
    }

    private StudyPostComment createChildComment(Study study, User user, Long parentCommentId) {
        CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
        return studyCommentService.writeChildComment(study.getStudyId(), parentCommentId, commentWriteRequest, user);
    }
}