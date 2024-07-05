package together.together_project.service;

import static together.together_project.domain.UserStudyJoinStatus.APPROVED;
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
public class UserStudyLinkService {

    private final StudyService studyService;
    private final UserStudyLinkRepositoryImpl userStudyLinkRepository;

    public void join(Long studyId, User user) {
        Study study = studyService.getById(studyId);

        // 이미 참여 신청한 유저인지 확인
        Optional<UserStudyLink> studyLink = userStudyLinkRepository.findByStudyIdAndUserId(study.getStudyId(),
                user.getId());
        if (studyLink.isPresent() && studyLink.get().getParticipant() == user) {
            throw new CustomException(ErrorCode.STUDY_ALREADY_JOINED);
        }

        UserStudyLink userStudyLink = UserStudyLink.toUserStudyLink(study, user);

        userStudyLinkRepository.save(userStudyLink);
        userStudyLink.pending();
    }


    public UserStudyJoinStatus respondToJoinRequest(RespondToJoinRequestDto request, Long studyId) {
        studyService.getById(studyId);
        UserStudyLink userStudyLink = userStudyLinkRepository.findByStudyIdAndUserId(studyId, request.userId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (request.isAccept()) {
            userStudyLink.approve();
            return APPROVED;
        }
        userStudyLink.reject();

        return REJECTED;
    }

    public List<UserStudyLink> getAllJoinRequest(Long studyId, Long cursor) {
        return userStudyLinkRepository.paginateUserStudyLink(studyId, cursor, PENDING);
    }

    public void deleteByStudyId(Long studyId) {
        userStudyLinkRepository.findByStudyId(studyId);
    }

    public List<UserStudyLink> getAllParticipants(Long studyId, Long cursor) {
        return userStudyLinkRepository.paginateUserStudyLink(studyId, cursor, APPROVED);
    }

    // 참여 신청 철회
    public void withdrawJoinStudyRequest(Long studyId, Long userId) {
        userStudyLinkRepository.findByStudyId(studyId, userId, PENDING)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_ALREADY_WITHDRAW))
                .softDelete();
    }

    // 참여 철회
    public void withdrawParticipation(Long studyId, User currentUser) {
        userStudyLinkRepository.findByStudyId(studyId, currentUser.getId(), APPROVED)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_ALREADY_WITHDRAW))
                .softDelete();

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
}
