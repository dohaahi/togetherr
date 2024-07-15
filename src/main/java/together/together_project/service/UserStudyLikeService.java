package together.together_project.service;

import static together.together_project.domain.UserStudyJoinStatus.APPROVED;
import static together.together_project.domain.UserStudyJoinStatus.LEADER;
import static together.together_project.domain.UserStudyJoinStatus.PENDING;
import static together.together_project.domain.UserStudyJoinStatus.REJECTED;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.domain.UserStudyJoinStatus;
import together.together_project.domain.UserStudyLink;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.UserStudyLinkRepositoryImpl;
import together.together_project.service.dto.request.RespondToJoinRequestDto;

@Service
@Transactional
@RequiredArgsConstructor
public class UserStudyLikeService {

    private final StudyService studyService;
    private final UserStudyLinkRepositoryImpl userStudyLinkRepository;

    public void join(Long studyId, User user) {
        Study study = studyService.getById(studyId);

        if (study.isFulled()) {
            throw new CustomException(ErrorCode.STUDY_IS_FULLED);
        }

        // 리더인지 or 이미 참여 신청한 유저인지 확인
        Optional<UserStudyLink> studyLink = userStudyLinkRepository
                .findByStudyIdAndUserId(study.getStudyId(), user.getId());

        if (studyLink.isPresent() && studyLink.get().getStatus() == LEADER) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        } else if (studyLink.isPresent() && studyLink.get().getParticipant() == user) {
            throw new CustomException(ErrorCode.STUDY_ALREADY_JOINED);
        }

        UserStudyLink userStudyLink = UserStudyLink.toUserStudyLink(study, user);

        userStudyLinkRepository.save(userStudyLink);
        userStudyLink.pending();
    }


    public UserStudyJoinStatus respondToJoinRequest(RespondToJoinRequestDto request, Long studyId, User user) {
        verifyUserIsStudyLeader(user, studyId);

        studyService.getById(studyId);
        UserStudyLink userStudyLink = userStudyLinkRepository.findByStudyIdAndUserId(studyId, request.userId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        if (request.isAccept()) {
            userStudyLink.approve();
            return APPROVED;
        }
        userStudyLink.reject();

        return REJECTED;
    }

    public List<UserStudyLink> getAllJoinRequest(Long studyId, Long cursor, User user) {
        verifyUserIsStudyLeader(user, studyId);

        return userStudyLinkRepository.paginateUserStudyLink(studyId, cursor, PENDING);
    }

    public List<UserStudyLink> getAllParticipants(Long studyId, Long cursor) {
        return userStudyLinkRepository.paginateUserStudyLink(studyId, cursor, APPROVED);
    }

    // 참여 신청 철회
    public void withdrawJoinStudyRequest(Long studyId, Long userId) {
        studyService.getById(studyId);

        UserStudyLink studyLink = userStudyLinkRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        if (studyLink.getStatus().equals(LEADER)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        } else if (studyLink.getStatus().equals(PENDING) && studyLink.isDeleted()) {
            throw new CustomException(ErrorCode.STUDY_ALREADY_WITHDRAW);
        }

        studyLink.softDelete();
    }

    // 참여 철회
    public void withdrawParticipation(Long studyId, Long userId) {
        UserStudyLink studyLink = userStudyLinkRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        if (studyLink.getStatus().equals(LEADER)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        } else if (studyLink.getStatus().equals(APPROVED) && studyLink.isDeleted()) {
            throw new CustomException(ErrorCode.STUDY_ALREADY_WITHDRAW);
        }

        studyLink.softDelete();

        studyService.getById(studyId)
                .decreaseParticipantCount();
    }

    // 유저가 탈퇴한 경우 참여한 스터디 자동 철회 신청
    public void withdrawByUserId(Long userId) {
        userStudyLinkRepository.findByUserId(userId)
                .forEach(userStudyLink -> {
                    userStudyLink.softDelete();
                    userStudyLink.getStudy().increaseParticipantCount();
                });
    }

    public void checkUserParticipant(Long studyId, Long userId) {
        UserStudyLink userStudyLink = userStudyLinkRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_ACCESS));

        if (!userStudyLink.getStatus().equals(APPROVED)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    public List<UserStudyLink> getAllParticipatingStudy(Long userId, Long cursor) {
        return userStudyLinkRepository.findPaginateAllParticipatingStudy(userId, cursor);
    }

    public List<UserStudyLink> getAll() {
        return userStudyLinkRepository.getAll();
    }

    private void verifyUserIsStudyLeader(User currentUser, Long studyId) {
        if (!currentUser.getId().equals(studyService.getById(studyId).getLeader().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}
