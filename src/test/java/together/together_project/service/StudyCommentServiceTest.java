package together.together_project.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPostComment;
import together.together_project.domain.User;
import together.together_project.repository.UserRepositoryImpl;
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

    @Autowired
    private UserRepositoryImpl userRepository;

    private SignupRequestDto request;
    private Study study;

    @BeforeEach
    public void setup() {
        request = new SignupRequestDto("aaa@google.com", "aaa", "a12345678!", null, null);
        userService.signup(request);

        User user = userRepository.findByEmail(request.email()).get();
        StudyPostCreateRequestDto studyPostCreateRequestDto = new StudyPostCreateRequestDto("title", "content",
                "location", 5);
        study = studyService.createStudyPost(studyPostCreateRequestDto, user);
    }

    @DisplayName("댓글 작성 가능")
    @Test
    public void writeComment() {
        User user = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        userRepository.save(user);

        CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
        StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);

        assertThat(comment.getParentCommentId()).isNull();
        assertThat(comment.getAuthor()).isEqualTo(user);
        assertThat(comment.getContent()).isEqualTo("content");
        assertThat(comment.getStudyPost()).isEqualTo(study.getStudyPost());
        assertThat(comment.getTotalLikeCount()).isEqualTo(0);
    }

    @DisplayName("댓글 수정 가능")
    @Test
    public void updateComment() {
        User user = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        userRepository.save(user);

        CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("new content");
        StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);

        CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto("update content");
        studyCommentService.updateComment(study.getStudyId(), comment.getId(), commentUpdateRequest);

        assertThat(comment.getParentCommentId()).isNull();
        assertThat(comment.getAuthor()).isEqualTo(user);
        assertThat(comment.getContent()).isEqualTo("update content");
        assertThat(comment.getStudyPost()).isEqualTo(study.getStudyPost());
        assertThat(comment.getTotalLikeCount()).isEqualTo(0);
    }

    @DisplayName("댓글 삭제 가능")
    @Test
    public void deleteComment() {
        User user = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        userRepository.save(user);

        CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("new content");
        StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);

        studyCommentService.withdrawComment(study.getStudyId(), comment.getId());

        assertThat(comment.getDeletedAt()).isNotNull();
    }

    @DisplayName("대댓글 작성 가능")
    @Test
    public void writeChildCommentComment() {
        User user = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        userRepository.save(user);

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

    @DisplayName("대댓글 수정 가능")
    @Test
    public void updateChildComment() {
        User user = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        userRepository.save(user);

        CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
        StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
        CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
        StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                childCommentWriteRequest, user);

        CommentUpdateRequestDto commentUpdateRequest = new CommentUpdateRequestDto("update child content");
        studyCommentService.updateChildComment(study.getStudyId(), comment.getId(), childComment.getId(),
                commentUpdateRequest);

        assertThat(childComment.getParentCommentId()).isEqualTo(comment.getId());
        assertThat(childComment.getAuthor()).isEqualTo(user);
        assertThat(childComment.getContent()).isEqualTo("update child content");
        assertThat(comment.getStudyPost()).isEqualTo(study.getStudyPost());
        assertThat(childComment.getTotalLikeCount()).isEqualTo(0);
    }

    @DisplayName("대댓글 삭제 가능")
    @Test
    public void withdrawChildComment() {
        User user = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        userRepository.save(user);

        CommentWriteRequestDto commentWriteRequest = new CommentWriteRequestDto("content");
        StudyPostComment comment = studyCommentService.write(commentWriteRequest, study.getStudyId(), user);
        CommentWriteRequestDto childCommentWriteRequest = new CommentWriteRequestDto("child content");
        StudyPostComment childComment = studyCommentService.writeChildComment(study.getStudyId(), comment.getId(),
                childCommentWriteRequest, user);

        studyCommentService.withdrawChildComment(study.getStudyId(), comment.getId(), childComment.getId());

        assertThat(childComment.getDeletedAt()).isNotNull();
    }

    @DisplayName("댓글 전체 조회 기능")
    @Test
    public void getAllComment() {
        User user = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        userRepository.save(user);

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
}