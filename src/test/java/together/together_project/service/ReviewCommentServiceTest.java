package together.together_project.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.repository.StudyPostRepositoryImpl;
import together.together_project.repository.UserRepositoryImpl;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;

@SpringBootTest
@Transactional
class ReviewCommentServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private StudyService studyService;

    @Autowired
    private StudyPostRepositoryImpl studyPostRepository;

    @Autowired
    private UserStudyLinkService userStudyLinkService;

    @DisplayName("스터디 참여자는 다른 참여자가 작성한 Review에 대한 comment 작성 가능")
    @Test
    public void test() {
        SignupRequestDto signupRequestA = new SignupRequestDto(
                "abc@google,com",
                "asd",
                "a123456789!",
                null,
                null
        );
        userService.signup(signupRequestA);
        User userA = userRepository.findByEmail(signupRequestA.email()).get();

        StudyPostCreateRequestDto studyPostCreateRequest = new StudyPostCreateRequestDto(
                "title",
                "content",
                "location",
                5
        );
        Study study = studyService.createStudyPost(studyPostCreateRequest, userA);

        SignupRequestDto signupRequestB = new SignupRequestDto(
                "hjk@google,com",
                "jkl",
                "a123456789!",
                null,
                null
        );
        userService.signup(signupRequestB);
        User userB = userRepository.findByEmail(signupRequestB.email()).get();
        userStudyLinkService.join(study.getStudyId(), userB);

        // 리뷰 작성
        // ReviewCommentService.createComment(userId, StudyId, reviewId)
    }
}