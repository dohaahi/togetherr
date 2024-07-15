package together.together_project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static together.together_project.domain.UserStudyJoinStatus.APPROVED;
import static together.together_project.domain.UserStudyJoinStatus.PENDING;
import static together.together_project.domain.UserStudyJoinStatus.REJECTED;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.domain.UserStudyJoinStatus;
import together.together_project.domain.UserStudyLink;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.UserStudyLinkRepositoryImpl;
import together.together_project.service.dto.request.RespondToJoinRequestDto;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;

@SpringBootTest
@Transactional
class UserStudyLikeServiceTest {

    @Autowired
    private UserStudyLikeService userStudyLikeService;

    @Autowired
    private UserService userService;

    @Autowired
    private StudyService studyService;

    @Autowired
    private UserStudyLinkRepositoryImpl userStudyLinkRepository;

    @BeforeEach
    public void setup() {
        User leader = createUser("leader@google.com", "leader");

        for (int i = 0; i < 5; i++) {
            createUser("user" + i + "@google.com", "user" + i);
        }

        for (int i = 0; i < 5; i++) {
            createStudyPost(leader);
        }
    }

    @Nested
    class getJoinRequest {

        @DisplayName("참여자 신청 목록 조회 가능")
        @Test
        public void testViewJoinRequest() {
            Study study = studyService.getAllStudy(null).get(0);

            for (int i = 0; i < 5; i++) {
                userStudyLikeService.join(study.getStudyId(), getUser(1 + i));
            }

            List<UserStudyLink> joinRequests = userStudyLikeService.getAllJoinRequest(study.getStudyId(), null,
                    getLeader());

            assertThat(joinRequests.size()).isEqualTo(5);
        }

        @DisplayName("리더가 아닌 사용자가 접근한 경우 예외 발셍")
        @Test
        void testGetJoinUser2() {
            Study study = studyService.getAllStudy(null).get(0);

            for (int i = 0; i < 5; i++) {
                userStudyLikeService.join(study.getStudyId(), getUser(1 + i));
            }

            assertThrows(CustomException.class,
                    () -> userStudyLikeService.getAllJoinRequest(study.getStudyId(), null, getUser(1)),
                    ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("더 이상 가져올 데이터가 없는 경우 예외 발셍")
        @Test
        void testGetJoinUser3() {
            Study study = studyService.getAllStudy(null).get(0);

            assertThrows(CustomException.class,
                    () -> userStudyLikeService.getAllJoinRequest(study.getStudyId(), null, getLeader()),
                    ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }
    }

    @Nested
    class getParticipant {

        @DisplayName("스터디 참여자 목록 조회 가능")
        @Test
        public void testViewParticipants() {
            Study study = studyService.getAllStudy(null).get(0);

            for (int i = 0; i < 4; i++) {
                userStudyLikeService.join(study.getStudyId(), getUser(1 + i));
                User user = getUser(i + 1);

                RespondToJoinRequestDto respondToJoinRequest = new RespondToJoinRequestDto(user.getId(), true);
                userStudyLikeService.respondToJoinRequest(respondToJoinRequest, study.getStudyId(), getLeader());
            }

            List<UserStudyLink> participants = userStudyLikeService.getAllParticipants(study.getStudyId(), null);

            assertThat(participants.size()).isEqualTo(4);
        }

        @DisplayName("더 이상 가져올 데이터가 없는 경우 예외 발생")
        @Test
        public void testViewParticipants2() {
            Study study = studyService.getAllStudy(null).get(0);

            assertThrows(CustomException.class, () -> userStudyLikeService.getAllParticipants(study.getStudyId(), null),
                    ErrorCode.DATA_NOT_FOUND.getDescription());
        }
    }

    @Nested
    class ResponseToJoinRequest {

        @DisplayName("참여 신청자에게 승인/거절 응답 가능")
        @Test
        public void testResponseToJoinRequest() {
            User user1 = getUser(1);
            User user2 = getUser(2);
            Study study = studyService.getAllStudy(null).get(0);

            userStudyLikeService.join(study.getStudyId(), user1);
            userStudyLikeService.join(study.getStudyId(), user2);

            RespondToJoinRequestDto respondToJoinRequest1 = new RespondToJoinRequestDto(user1.getId(), true);
            UserStudyJoinStatus joinStatus1 = userStudyLikeService.respondToJoinRequest(respondToJoinRequest1,
                    study.getStudyId(), getLeader());

            RespondToJoinRequestDto respondToJoinRequest2 = new RespondToJoinRequestDto(user2.getId(), false);
            UserStudyJoinStatus joinStatus2 = userStudyLikeService.respondToJoinRequest(respondToJoinRequest2,
                    study.getStudyId(), getLeader());

            assertThat(joinStatus1).isEqualTo(APPROVED);
            assertThat(study.getParticipantCount()).isEqualTo(2);

            assertThat(joinStatus2).isEqualTo(REJECTED);
        }

        @DisplayName("리더가 아닌 사용자가 접근한 경우 예외 발생")
        @Test
        public void testResponseToJoinRequest2() {
            User user1 = getUser(1);
            User user2 = getUser(2);
            User user3 = getUser(3);
            Study study = studyService.getAllStudy(null).get(0);

            userStudyLikeService.join(study.getStudyId(), user1);
            userStudyLikeService.join(study.getStudyId(), user2);

            RespondToJoinRequestDto respondToJoinRequest1 = new RespondToJoinRequestDto(user1.getId(), true);
            assertThrows(CustomException.class,
                    () -> userStudyLikeService.respondToJoinRequest(respondToJoinRequest1, study.getStudyId(), user3),
                    ErrorCode.DATA_NOT_FOUND.getDescription());

            RespondToJoinRequestDto respondToJoinRequest2 = new RespondToJoinRequestDto(user2.getId(), false);
            assertThrows(CustomException.class,
                    () -> userStudyLikeService.respondToJoinRequest(respondToJoinRequest2, study.getStudyId(), user3),
                    ErrorCode.DATA_NOT_FOUND.getDescription());
        }

        @DisplayName("유효하지 않은 요청자일 경우 예외 발생")
        @Test
        public void testResponseToJoinRequest3() {
            User user1 = getUser(1);
            User user2 = getUser(2);
            User user3 = getUser(3);
            Study study = studyService.getAllStudy(null).get(0);

            userStudyLikeService.join(study.getStudyId(), user1);
            userStudyLikeService.join(study.getStudyId(), user2);

            RespondToJoinRequestDto respondToJoinRequest1 = new RespondToJoinRequestDto(user3.getId(), true);
            assertThrows(CustomException.class,
                    () -> userStudyLikeService.respondToJoinRequest(respondToJoinRequest1, study.getStudyId(),
                            getLeader()), ErrorCode.INVALID_REQUEST.getDescription());

            RespondToJoinRequestDto respondToJoinRequest2 = new RespondToJoinRequestDto(user3.getId(), false);
            assertThrows(CustomException.class,
                    () -> userStudyLikeService.respondToJoinRequest(respondToJoinRequest2, study.getStudyId(),
                            getLeader()), ErrorCode.INVALID_REQUEST.getDescription());
        }

        @DisplayName("이미 승인된 유저를 다시 승인하는 경우 예외 발생")
        @Test
        public void testResponseToJoinRequest4() {
            User user = getUser(1);
            Study study = studyService.getAllStudy(null).get(0);

            userStudyLikeService.join(study.getStudyId(), user);

            RespondToJoinRequestDto respondToJoinRequest = new RespondToJoinRequestDto(user.getId(), true);
            userStudyLikeService.respondToJoinRequest(respondToJoinRequest, study.getStudyId(), getLeader());

            assertThrows(CustomException.class,
                    () -> userStudyLikeService.respondToJoinRequest(respondToJoinRequest, study.getStudyId(),
                            getLeader()), ErrorCode.INVALID_REQUEST.getDescription());
        }

        @DisplayName("유효하지 않은 스터디인 경우 예외 발생")
        @Test
        public void testResponseToJoinRequest5() {
            User user = getUser(1);
            Study study = studyService.getAllStudy(null).get(0);

            userStudyLikeService.join(study.getStudyId(), user);

            RespondToJoinRequestDto respondToJoinRequest = new RespondToJoinRequestDto(user.getId(), true);

            assertThrows(CustomException.class,
                    () -> userStudyLikeService.respondToJoinRequest(respondToJoinRequest, study.getStudyId() + 1L,
                            getLeader()), ErrorCode.STUDY_NOT_FOUND.getDescription());
        }
    }

    @Nested
    class Join {

        @DisplayName("스터디 참여 가능")
        @Test
        public void testJoin() {
            User user = getUser(1);
            Study study = studyService.getAllStudy(null).get(0);

            userStudyLikeService.join(study.getStudyId(), user);

            UserStudyLink userStudyLink = userStudyLikeService.getAll().get(0);

            assertThat(userStudyLink.getStudy()).isEqualTo(study);
            assertThat(userStudyLink.getParticipant()).isEqualTo(user);
            assertThat(userStudyLink.getStatus()).isEqualTo(PENDING);
        }

        @DisplayName("리더가 참여 신청한 경우 예외 발생")
        @Test
        public void testJoin2() {
            Study study = studyService.getAllStudy(null).get(0);

            assertThrows(CustomException.class, () -> userStudyLikeService.join(study.getStudyId(), getLeader()),
                    ErrorCode.STUDY_NOT_FOUND.getDescription());
        }

        @DisplayName("유효하지 않은 스터디인 경우 예외 발생")
        @Test
        public void testJoin3() {
            User user = getUser(1);
            Study study = studyService.getAllStudy(null).get(0);

            assertThrows(CustomException.class, () -> userStudyLikeService.join(study.getStudyId() + 100L, user),
                    ErrorCode.STUDY_NOT_FOUND.getDescription());
        }

        @DisplayName("이미 신청한 스터디인 경우 예외 발생")
        @Test
        public void testJoin4() {
            User user = getUser(1);
            Study study = studyService.getAllStudy(null).get(0);
            userStudyLikeService.join(study.getStudyId(), user);

            assertThrows(CustomException.class, () -> userStudyLikeService.join(study.getStudyId(), user),
                    ErrorCode.STUDY_NOT_FOUND.getDescription());
        }

        @DisplayName("이미 모집 완료된 스터디인 경우 예외 발생")
        @Test
        public void testJoin5() {
            Study study = studyService.getAllStudy(null).get(0);
            for (int i = 0; i < 4; i++) {
                User user = getUser(1 + i);
                userStudyLikeService.join(study.getStudyId(), user);
                RespondToJoinRequestDto respondToJoinRequest = new RespondToJoinRequestDto(user.getId(), true);
                userStudyLikeService.respondToJoinRequest(respondToJoinRequest, study.getStudyId(), getLeader());
            }

            assertThrows(CustomException.class, () -> userStudyLikeService.join(study.getStudyId(), getUser(5)),
                    ErrorCode.STUDY_IS_FULLED.getDescription());
        }
    }

    @Nested
    class withdrawJoin {

        @DisplayName("스터디 참여 신청한 유저는 참여 신청 철회 가능")
        @Test
        public void testWithdrawStudyJoin() {
            User user = getUser(1);
            Study study = studyService.getAllStudy(null).get(0);

            userStudyLikeService.join(study.getStudyId(), user);
            UserStudyLink userStudyLink = userStudyLikeService.getAll().get(0);

            userStudyLikeService.withdrawJoinStudyRequest(study.getStudyId(), user.getId());

            assertThat(userStudyLink.getDeletedAt()).isNotNull();
        }

        @DisplayName("유효하지 않은 스터디인 경우 예외 발생")
        @Test
        public void testWithdrawStudyJoin2() {
            User user = getUser(1);
            Study study = studyService.getAllStudy(null).get(0);

            userStudyLikeService.join(study.getStudyId(), user);

            studyService.deleteStudy(study.getStudyId(), getLeader());

            assertThrows(CustomException.class,
                    () -> userStudyLikeService.withdrawJoinStudyRequest(study.getStudyId(), user.getId()),
                    ErrorCode.STUDY_NOT_FOUND.getDescription());
        }

        @DisplayName("이미 철회 신청한 스터디인 경우 예외 발생")
        @Test
        public void testWithdrawStudyJoin3() {
            User user = getUser(1);
            Study study = studyService.getAllStudy(null).get(0);

            userStudyLikeService.join(study.getStudyId(), user);
            userStudyLikeService.withdrawJoinStudyRequest(study.getStudyId(), user.getId());

            assertThrows(CustomException.class,
                    () -> userStudyLikeService.withdrawJoinStudyRequest(study.getStudyId(), user.getId()),
                    ErrorCode.STUDY_ALREADY_WITHDRAW.getDescription());
        }
    }

    @Nested
    class withdrawParticipant {

        @DisplayName("스터디 참여 중인 유저는 참여 철회 가능")
        @Test
        public void testWithdrawParticipant() {
            User user = getUser(1);
            Study study = studyService.getAllStudy(null).get(0);

            userStudyLikeService.join(study.getStudyId(), user);
            UserStudyLink userStudyLink = userStudyLinkRepository.findByStudyIdAndUserId(study.getStudyId(),
                            user.getId())
                    .get();
            RespondToJoinRequestDto respondToJoinRequest = new RespondToJoinRequestDto(user.getId(), true);
            userStudyLikeService.respondToJoinRequest(respondToJoinRequest, study.getStudyId(), getLeader());

            userStudyLikeService.withdrawParticipation(study.getStudyId(), user.getId());

            assertThat(userStudyLink.getDeletedAt()).isNotNull();
            assertThat(study.getParticipantCount()).isEqualTo(1);
        }

        @DisplayName("리더가 참여 철회한 경우 예외 발생")
        @Test
        public void testWithdrawParticipant2() {
            Study study = studyService.getAllStudy(null).get(0);

            assertThrows(CustomException.class,
                    () -> userStudyLikeService.withdrawParticipation(study.getStudyId(), getLeader().getId()),
                    ErrorCode.INVALID_REQUEST.getDescription());
        }

        @DisplayName("유효하지 않은 스터디인 경우 예외 발생")
        @Test
        public void testWithdrawParticipant3() {
            User user = getUser(1);
            Study study = studyService.getAllStudy(null).get(0);

            userStudyLikeService.join(study.getStudyId(), user);

            studyService.deleteStudy(study.getStudyId(), getLeader());

            assertThrows(CustomException.class,
                    () -> userStudyLikeService.withdrawParticipation(study.getStudyId(), user.getId()),
                    ErrorCode.STUDY_NOT_FOUND.getDescription());
        }

        @DisplayName("이미 철회 신청한 경우 예외 발생")
        @Test
        public void testWithdrawParticipant4() {
            User user = getUser(1);
            Study study = studyService.getAllStudy(null).get(0);

            userStudyLikeService.join(study.getStudyId(), user);
            RespondToJoinRequestDto respondToJoinRequest = new RespondToJoinRequestDto(user.getId(), true);
            userStudyLikeService.respondToJoinRequest(respondToJoinRequest, study.getStudyId(), getLeader());

            userStudyLikeService.withdrawParticipation(study.getStudyId(), user.getId());

            assertThrows(CustomException.class,
                    () -> userStudyLikeService.withdrawParticipation(study.getStudyId(), user.getId()),
                    ErrorCode.INVALID_REQUEST.getDescription());
        }
    }


    private User getLeader() {
        Long userId = userService.getAllId().get(0);
        return userService.getUserById(userId);
    }

    private User getUser(int index) {
        Long userId = userService.getAllId().get(index);
        return userService.getUserById(userId);
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
}