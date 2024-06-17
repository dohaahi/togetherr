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
import together.together_project.domain.StudyPost;
import together.together_project.domain.User;
import together.together_project.repository.UserRepositoryImpl;
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

    @Autowired
    private UserRepositoryImpl userRepository;

    SignupRequestDto request;

    @BeforeEach
    public void setup() {
        request = new SignupRequestDto(
                "aaa@google.com",
                "aaa",
                "a12345678!",
                null,
                null
        );

        userService.signup(request);
    }

    @DisplayName("비회원은 모든 스터디 게시글 조회 가능")
    @Test
    public void testAllView() {
        User user = userRepository.findByEmail(request.email()).get();
        StudyPostCreateRequestDto studyPostCreateRequest1 = new StudyPostCreateRequestDto("title1,", "content1",
                "location1", 5);
        StudyPostCreateRequestDto studyPostCreateRequest2 = new StudyPostCreateRequestDto("title2,", "content2",
                "location2", 5);
        StudyPostCreateRequestDto studyPostCreateRequest3 = new StudyPostCreateRequestDto("title3,", "content3",
                "location3", 5);
        StudyPostCreateRequestDto studyPostCreateRequest4 = new StudyPostCreateRequestDto("title4,", "content4",
                "location4", 5);
        StudyPostCreateRequestDto studyPostCreateRequest5 = new StudyPostCreateRequestDto("title5,", "content5",
                "location5", 5);
        studyService.createStudyPost(studyPostCreateRequest1, user);
        studyService.createStudyPost(studyPostCreateRequest2, user);
        studyService.createStudyPost(studyPostCreateRequest3, user);
        studyService.createStudyPost(studyPostCreateRequest4, user);
        studyService.createStudyPost(studyPostCreateRequest5, user);

        List<Study> allStudy = studyService.getAllStudy(null);

        assertThat(allStudy.size()).isEqualTo(5);
    }

    @DisplayName("비회원은 스터디 게시글 상세 조회 가능")
    @Test
    public void testViewStudy() {
        User user = userRepository.findByEmail(request.email()).get();
        StudyPostCreateRequestDto studyPostCreateRequestDto = new StudyPostCreateRequestDto(
                "title",
                "content",
                "location",
                5
        );
        Study study = studyService.createStudyPost(studyPostCreateRequestDto, user);

        Study viewStudy = studyService.getById(1L);
        StudyPost studyPost = viewStudy.getStudyPost();

        assertThat(study.getParticipantCount()).isEqualTo(1);
        assertThat(study.getLocation()).isEqualTo("location");
        assertThat(study.getMaxPeople()).isEqualTo(5);
        assertThat(study.getLeader()).isEqualTo(user);

        assertThat(studyPost.getTitle()).isEqualTo("title");
        assertThat(studyPost.getContent()).isEqualTo("content");
        assertThat(studyPost.getTotalLikeCount()).isEqualTo(0);
        assertThat(studyPost.getRefreshedAt()).isNull();
    }

    @DisplayName("가입된 사용자는 스터디 생성 가능")
    @Test
    public void test() {
        SignupRequestDto signupRequest = new SignupRequestDto(
                "abc@google.com",
                "kkk",
                "a123456789!!",
                null,
                null);

        userService.signup(signupRequest);
        User user = userRepository.findByEmail(signupRequest.email()).get();

        StudyPostCreateRequestDto studyRequest = new StudyPostCreateRequestDto("title", "content", "city",
                5);
        Study study = studyService.createStudyPost(studyRequest, user);

        StudyPost studyPost = study.getStudyPost();

        assertThat(study.getParticipantCount()).isEqualTo(1);
        assertThat(study.getLocation()).isEqualTo("city");
        assertThat(study.getMaxPeople()).isEqualTo(5);
        assertThat(study.getLeader()).isEqualTo(user);

        assertThat(studyPost.getTitle()).isEqualTo("title");
        assertThat(studyPost.getContent()).isEqualTo("content");
        assertThat(studyPost.getTotalLikeCount()).isEqualTo(0);
        assertThat(studyPost.getRefreshedAt()).isNull();
    }

    @DisplayName("스터디 리더는 스터디 게시글 수정 가능")
    @Test
    public void test2() {
        User user = userRepository.findByEmail(request.email()).get();
        StudyPostCreateRequestDto studyPostCreateRequestDto = new StudyPostCreateRequestDto(
                "title",
                "content",
                "location",
                5
        );
        Study study = studyService.createStudyPost(studyPostCreateRequestDto, user);

        StudyPostUpdateRequestDto studyPostUpdateRequestDto = new StudyPostUpdateRequestDto(
                "new title",
                "new content",
                "new location",
                10
        );
        Study updatedStudy = studyService.updateStudyPost(study.getStudyId(), studyPostUpdateRequestDto);
        StudyPost updatedStudyPost = updatedStudy.getStudyPost();

        assertThat(updatedStudy.getLeader()).isEqualTo(user);
        assertThat(updatedStudy.getParticipantCount()).isEqualTo(1);
        assertThat(updatedStudy.getLocation()).isEqualTo("new location");
        assertThat(updatedStudy.getMaxPeople()).isEqualTo(10);

        assertThat(updatedStudyPost.getTitle()).isEqualTo("new title");
        assertThat(updatedStudyPost.getContent()).isEqualTo("new content");
        assertThat(updatedStudyPost.getTotalLikeCount()).isEqualTo(0);
        assertThat(updatedStudyPost.getRefreshedAt()).isNull();
    }

    @DisplayName("스터디 리더는 스터디 게시글 삭제 가능")
    @Test
    public void testDelete() {
        User user = userRepository.findByEmail(request.email()).get();
        StudyPostCreateRequestDto studyPostCreateRequestDto = new StudyPostCreateRequestDto(
                "title",
                "content",
                "location",
                5
        );
        Study study = studyService.createStudyPost(studyPostCreateRequestDto, user);

        studyService.deleteStudy(study.getStudyId());

        assertThat(study.getDeletedAt()).isNotNull();
    }
}