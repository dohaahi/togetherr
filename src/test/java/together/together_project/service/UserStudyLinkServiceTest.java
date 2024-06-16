package together.together_project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static together.together_project.domain.UserStudyJoinStatus.APPROVED;
import static together.together_project.domain.UserStudyJoinStatus.PENDING;
import static together.together_project.domain.UserStudyJoinStatus.REJECTED;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.domain.UserStudyJoinStatus;
import together.together_project.domain.UserStudyLink;
import together.together_project.repository.UserRepositoryImpl;
import together.together_project.repository.UserStudyLinkRepositoryImpl;
import together.together_project.service.dto.request.RespondToJoinRequestDto;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;

@SpringBootTest
@Transactional
class UserStudyLinkServiceTest {

    @Autowired
    private UserStudyLinkService userStudyLinkService;

    @Autowired
    private UserService userService;

    @Autowired
    private StudyService studyService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private UserStudyLinkRepositoryImpl userStudyLinkRepository;

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

    @DisplayName("참여자 신청 목록 조회 가능")
    @Test
    public void testViewJoinRequest() {
        User user1 = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        User user2 = new User(3L, "ddd@abc.com", "ddd", "a123", null, null);
        User user3 = new User(4L, "fff@abc.com", "fff", "a123", null, null);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        UserStudyLink userStudyLink1 = new UserStudyLink(1L, study, user1, UserStudyJoinStatus.PENDING);
        UserStudyLink userStudyLink2 = new UserStudyLink(2L, study, user2, UserStudyJoinStatus.PENDING);
        UserStudyLink userStudyLink3 = new UserStudyLink(3L, study, user3, UserStudyJoinStatus.PENDING);
        userStudyLinkRepository.save(userStudyLink1);
        userStudyLinkRepository.save(userStudyLink2);
        userStudyLinkRepository.save(userStudyLink3);

        List<UserStudyLink> joinRequests = userStudyLinkService.getAllJoinRequest(study.getStudyId(), null);

        assertThat(joinRequests.size()).isEqualTo(3);
    }

    @DisplayName("스터디 참여자 목록 조회 가능")
    @Test
    public void testViewParticipants() {
        User user1 = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        User user2 = new User(3L, "ddd@abc.com", "ddd", "a123", null, null);
        User user3 = new User(4L, "fff@abc.com", "fff", "a123", null, null);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        UserStudyLink userStudyLink1 = new UserStudyLink(1L, study, user1, APPROVED);
        UserStudyLink userStudyLink2 = new UserStudyLink(2L, study, user2, APPROVED);
        UserStudyLink userStudyLink3 = new UserStudyLink(3L, study, user3, APPROVED);
        userStudyLinkRepository.save(userStudyLink1);
        userStudyLinkRepository.save(userStudyLink2);
        userStudyLinkRepository.save(userStudyLink3);

        List<UserStudyLink> participants = userStudyLinkService.getAllParticipants(study.getStudyId(), null);

        assertThat(participants.size()).isEqualTo(3);
    }

    @DisplayName("참여 신청자에게 승인/거절 응답 가능")
    @Test
    public void testResponseToJoinRequest() {
        User user1 = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        userRepository.save(user1);
        UserStudyLink userStudyLink1 = new UserStudyLink(1L, study, user1, UserStudyJoinStatus.PENDING);
        userStudyLinkRepository.save(userStudyLink1);

        User user2 = new User(3L, "ddd@abc.com", "ddd", "a123", null, null);
        userRepository.save(user2);
        UserStudyLink userStudyLink2 = new UserStudyLink(2L, study, user2, UserStudyJoinStatus.PENDING);
        userStudyLinkRepository.save(userStudyLink2);

        RespondToJoinRequestDto respondToJoinRequest1 = new RespondToJoinRequestDto(user1.getId(), true);
        UserStudyJoinStatus joinStatus1 = userStudyLinkService.respondToJoinRequest(respondToJoinRequest1,
                study.getStudyId());

        RespondToJoinRequestDto respondToJoinRequest2 = new RespondToJoinRequestDto(user2.getId(), false);
        UserStudyJoinStatus joinStatus2 = userStudyLinkService.respondToJoinRequest(respondToJoinRequest2,
                study.getStudyId());

        assertThat(joinStatus1).isEqualTo(APPROVED);
        assertThat(study.getParticipantCount()).isEqualTo(2);

        assertThat(joinStatus2).isEqualTo(REJECTED);
    }

    @DisplayName("스터디 참여 가능")
    @Test
    public void testJoin() {
        User user = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        userRepository.save(user);

        userStudyLinkService.join(study.getStudyId(), user);
        UserStudyLink userStudyLink = userStudyLinkRepository.findByStudyIdAndUserId(study.getStudyId(), user.getId())
                .get();

        assertThat(userStudyLink.getStudy()).isEqualTo(study);
        assertThat(userStudyLink.getParticipant()).isEqualTo(user);
        assertThat(userStudyLink.getStatus()).isEqualTo(PENDING);
    }

    @DisplayName("스터디 참여 신청한 유저는 참여 신청 철회 가능")
    @Test
    public void testWithdrawStudyJoin() {
        User user = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        userRepository.save(user);

        userStudyLinkService.join(study.getStudyId(), user);
        UserStudyLink userStudyLink = userStudyLinkRepository.findByStudyIdAndUserId(study.getStudyId(), user.getId())
                .get();

        userStudyLinkService.withdrawJoinStudyRequest(study.getStudyId(), user.getId());

        assertThat(userStudyLink.getDeletedAt()).isNotNull();
    }

    @DisplayName("스터디 참여 중인 유저는 참여 철회 가능")
    @Test
    public void testWithdrawParticipant() {
        User user = new User(2L, "ccc@abc.com", "ccc", "a123", null, null);
        userRepository.save(user);

        userStudyLinkService.join(study.getStudyId(), user);
        UserStudyLink userStudyLink = userStudyLinkRepository.findByStudyIdAndUserId(study.getStudyId(), user.getId())
                .get();
        RespondToJoinRequestDto respondToJoinRequest = new RespondToJoinRequestDto(user.getId(), true);
        userStudyLinkService.respondToJoinRequest(respondToJoinRequest, study.getStudyId());

        userStudyLinkService.withdrawParticipation(study.getStudyId(), user);

        assertThat(userStudyLink.getDeletedAt()).isNotNull();
        assertThat(study.getParticipantCount()).isEqualTo(1);
    }
}