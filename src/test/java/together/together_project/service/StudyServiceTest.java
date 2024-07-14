package together.together_project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
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
import together.together_project.domain.StudyPost;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;
import together.together_project.service.dto.request.StudyPostUpdateRequestDto;

@SpringBootTest
@Transactional
class StudyServiceTest { // IntegrationTest

    @Autowired
    private UserService userService;

    @Autowired
    private StudyService studyService;

    @BeforeEach
    public void setup() {
        SignupRequestDto request = new SignupRequestDto("aaa@google.com", "aaa", "a12345678!", null, null);

        userService.signup(request);
    }

    @Nested
    class getAllStudy {

        @DisplayName("모든 스터디 게시글 조회")
        @Test
        public void testAllStudy() {
            User user = getUser();

            for (int i = 0; i < 5; i++) {
                createStudyPost("title" + i, "content" + i, "location" + i, user);
            }

            List<Study> allStudy = studyService.getAllStudy(null);

            assertThat(allStudy.size()).isEqualTo(5);
        }

        @Test
        void testAllStudy2() {
            assertThrows(CustomException.class, () -> studyService.getAllStudy(null),
                    ErrorCode.DATA_NOT_FOUND.getDescription());
        }
    }

    @Nested
    class getStudy {

        @DisplayName("스터디 게시글 상세 조회")
        @Test
        public void testViewStudy() {
            User user = getUser();

            LocalDateTime now = LocalDateTime.now();
            createStudyPost(user);
            Long studyId = studyService.getAllStudy(null).get(0).getStudyId();

            Study study = studyService.getById(studyId);
            StudyPost studyPost = study.getStudyPost();

            assertThat(study.getParticipantCount()).isEqualTo(1);
            assertThat(study.getLocation()).isEqualTo("location");
            assertThat(study.getMaxPeople()).isEqualTo(5);
            assertThat(study.getLeader()).isEqualTo(user);
            assertThat(studyPost.getCreatedAt()).isAfter(now);
            assertThat(studyPost.getUpdatedAt()).isAfter(now);
            assertThat(studyPost.getDeletedAt()).isNull();

            assertThat(studyPost.getTitle()).isEqualTo("title");
            assertThat(studyPost.getContent()).isEqualTo("content");
            assertThat(studyPost.getTotalLikeCount()).isEqualTo(0);
            assertThat(studyPost.getCreatedAt()).isAfter(now);
            assertThat(studyPost.getUpdatedAt()).isAfter(now);
            assertThat(studyPost.getDeletedAt()).isNull();
            assertThat(studyPost.getRefreshedAt()).isNull();
        }

        @Test
        void testStudy() {
            assertThrows(CustomException.class, () -> studyService.getById(10L),
                    ErrorCode.STUDY_NOT_FOUND.getDescription());
        }
    }

    @Nested
    class CreateStudy {

        @DisplayName("스터디 생성")
        @Test
        public void test() {
            User user = getUser();

            LocalDateTime now = LocalDateTime.now();
            createStudyPost(user);

            Study study = studyService.getAllStudy(null).get(0);
            StudyPost studyPost = study.getStudyPost();

            assertThat(study.getParticipantCount()).isEqualTo(1);
            assertThat(study.getLocation()).isEqualTo("location");
            assertThat(study.getMaxPeople()).isEqualTo(5);
            assertThat(study.getLeader()).isEqualTo(user);
            assertThat(studyPost.getCreatedAt()).isAfter(now);
            assertThat(studyPost.getUpdatedAt()).isAfter(now);
            assertThat(studyPost.getDeletedAt()).isNull();

            assertThat(studyPost.getTitle()).isEqualTo("title");
            assertThat(studyPost.getContent()).isEqualTo("content");
            assertThat(studyPost.getTotalLikeCount()).isEqualTo(0);
            assertThat(studyPost.getCreatedAt()).isAfter(now);
            assertThat(studyPost.getUpdatedAt()).isAfter(now);
            assertThat(studyPost.getDeletedAt()).isNull();
            assertThat(studyPost.getRefreshedAt()).isNull();
        }

//        @DisplayName("제목을 입력하지 않은 경우 예외 발생")
//        @Test
//        void testCreateStudy2() {
//            User user = getUser();
//
//            StudyPostCreateRequestDto studyPostCreateRequest = new StudyPostCreateRequestDto(
//                    null,
//                    "content",
//                    "location",
//                    5);
//
//            assertThrows(Exception.class,
//                    () -> studyService.createStudyPost(studyPostCreateRequest, user),
//                    "제목을 입력하지 않았습니다.");
//        }
//
//        @DisplayName("내용을 입력하지 않은 경우 예외 발생")
//        @Test
//        void testCreateStudy3() {
//            User user = getUser();
//
//            StudyPostCreateRequestDto studyPostCreateRequest = new StudyPostCreateRequestDto(
//                    "title",
//                    null,
//                    "location",
//                    5);
//
//            assertThrows(Exception.class,
//                    () -> studyService.createStudyPost(studyPostCreateRequest, user),
//                    "내용을 입력하지 않았습니다.");
//        }
//
//        @DisplayName("위치를 입력하지 않은 경우 예외 발생")
//        @Test
//        void testCreateStudy4() {
//            User user = getUser();
//
//            StudyPostCreateRequestDto studyPostCreateRequest = new StudyPostCreateRequestDto(
//                    "title",
//                    "content",
//                    null,
//                    5);
//
//            assertThrows(Exception.class,
//                    () -> studyService.createStudyPost(studyPostCreateRequest, user),
//                    "위치를 입력하지 않았습니다.");
//        }

        @DisplayName("최대 참여 인원을 입력하지 않은 경우 예외 발생")
        @Test
        void testCreateStudy5() {
            User user = getUser();

            StudyPostCreateRequestDto studyPostCreateRequest = new StudyPostCreateRequestDto("title", "content",
                    "location", null);

            assertThrows(Exception.class, () -> studyService.createStudyPost(studyPostCreateRequest, user),
                    "최대 인원을 입력하지 않았습니다.");
        }

        @DisplayName("제목을 입력하지 않은 경우 예외 발생")
        @ParameterizedTest
        @ValueSource(ints = {0, 1})
        void testCreateStudy6(int number) {
            User user = getUser();

            StudyPostCreateRequestDto studyPostCreateRequest = new StudyPostCreateRequestDto(null, "content",
                    "location", number);

            assertThrows(Exception.class, () -> studyService.createStudyPost(studyPostCreateRequest, user),
                    ErrorCode.MAX_PEOPLE_UNDER_LIMIT.getDescription());
        }
    }

    @Nested
    class UpdateStudy {

        @DisplayName("스터디 리더는 스터디 게시글 수정 가능")
        @Test
        public void testUpdate() {
            User user = getUser();
            createStudyPost(user);
            Study study = studyService.getAllStudy(null).get(0);

            LocalDateTime now = LocalDateTime.now();
            StudyPostUpdateRequestDto studyPostUpdateRequestDto = new StudyPostUpdateRequestDto("new title",
                    "new content", "new location", 10);
            Study updatedStudy = studyService.updateStudyPost(study.getStudyId(), studyPostUpdateRequestDto, user);
            StudyPost updatedStudyPost = updatedStudy.getStudyPost();

            assertThat(updatedStudy.getLeader()).isEqualTo(user);
            assertThat(updatedStudy.getParticipantCount()).isEqualTo(1);
            assertThat(updatedStudy.getLocation()).isEqualTo("new location");
            assertThat(updatedStudy.getMaxPeople()).isEqualTo(10);
            assertThat(updatedStudy.getUpdatedAt()).isAfter(now);
            assertThat(updatedStudy.getDeletedAt()).isNull();

            assertThat(updatedStudyPost.getTitle()).isEqualTo("new title");
            assertThat(updatedStudyPost.getContent()).isEqualTo("new content");
            assertThat(updatedStudyPost.getTotalLikeCount()).isEqualTo(0);
            assertThat(updatedStudyPost.getUpdatedAt()).isAfter(now);
            assertThat(updatedStudyPost.getDeletedAt()).isNull();
            assertThat(updatedStudyPost.getRefreshedAt()).isNull();
        }

        @DisplayName("작성자가 아닌 경우 예외 발생")
        @Test
        void testUpdate2() {
            User user = getUser();
            createStudyPost(user);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostUpdateRequestDto studyPostUpdateRequest = new StudyPostUpdateRequestDto("new title", "new content",
                    "new location", 10);

            assertThrows(CustomException.class,
                    () -> studyService.updateStudyPost(study.getStudyId(), studyPostUpdateRequest,
                            getUser(user.getId() + 1L)), ErrorCode.UNAUTHORIZED_ACCESS.getDescription());

        }

        @DisplayName("존재하지 않는 게시글을 수정하는 경우 예외 발생")
        @Test
        void testUpdate3() {
            User user = getUser();
            createStudyPost(user);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostUpdateRequestDto studyPostUpdateRequest = new StudyPostUpdateRequestDto("new title", "new content",
                    "new location", 10);

            assertThrows(CustomException.class,
                    () -> studyService.updateStudyPost(study.getStudyId() + 1L, studyPostUpdateRequest, user),
                    ErrorCode.UNAUTHORIZED_ACCESS.getDescription());

        }

        @DisplayName("title에 공백이 입력된 경우 예외 발생")
        @Test
        void testUpdate4() {
            User user = getUser();
            createStudyPost(user);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostUpdateRequestDto studyPostUpdateRequest = new StudyPostUpdateRequestDto("        ", "new content",
                    "new location", 10);

            assertThrows(CustomException.class,
                    () -> studyService.updateStudyPost(study.getStudyId(), studyPostUpdateRequest, user),
                    ErrorCode.EMPTY_CONTENT_ERROR.getDescription());

        }

        @DisplayName("content에 공백이 입력된 경우 예외 발생")
        @Test
        void testUpdate5() {
            User user = getUser();
            createStudyPost(user);
            Study study = studyService.getAllStudy(null).get(0);

            StudyPostUpdateRequestDto studyPostUpdateRequest = new StudyPostUpdateRequestDto("new Title", "       ",
                    "new location", 10);

            assertThrows(CustomException.class,
                    () -> studyService.updateStudyPost(study.getStudyId(), studyPostUpdateRequest, user),
                    ErrorCode.EMPTY_CONTENT_ERROR.getDescription());

        }
    }

    @Nested
    class StudyDelete {

        @DisplayName("스터디 게시글 삭제")
        @Test
        public void testDelete() {
            User user = getUser();
            createStudyPost(user);
            Study study = studyService.getAllStudy(null).get(0);

            studyService.deleteStudy(study.getStudyId(), user);

            assertThat(study.getDeletedAt()).isNotNull();
        }

        @DisplayName("작성자가 아닌 경우 예외 발생")
        @Test
        void testDelete2() {
            User user = getUser();
            createStudyPost(user);
            Study study = studyService.getAllStudy(null).get(0);

            assertThrows(CustomException.class,
                    () -> studyService.deleteStudy(study.getStudyId(), getUser(user.getId() + 1L)),
                    ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("존재하지 않는 스터디를 삭제하는 경우 예외 발생")
        @Test
        void testDelete3() {
            User user = getUser();
            createStudyPost(user);
            Study study = studyService.getAllStudy(null).get(0);

            assertThrows(CustomException.class, () -> studyService.deleteStudy(study.getStudyId() + 1L, user),
                    ErrorCode.STUDY_NOT_FOUND.getDescription());
        }
    }

    private User getUser() {
        Long userId = userService.getAllId().get(0);
        return userService.getUserById(userId);
    }

    private User getUser(Long id) {
        return userService.getUserById(id);
    }

    private void createStudyPost(User user) {
        StudyPostCreateRequestDto studyPostCreateRequest = new StudyPostCreateRequestDto("title", "content", "location",
                5);

        studyService.createStudyPost(studyPostCreateRequest, user);
    }

    private void createStudyPost(String title, String content, String location, User user) {
        StudyPostCreateRequestDto studyPostCreateRequest = new StudyPostCreateRequestDto(title, content, location, 5);

        studyService.createStudyPost(studyPostCreateRequest, user);
    }
}