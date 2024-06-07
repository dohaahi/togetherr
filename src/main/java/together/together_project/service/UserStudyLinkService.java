package together.together_project.service;

import static together.together_project.domain.UserStudyJoinStatus.APPROVED;
import static together.together_project.domain.UserStudyJoinStatus.PENDING;

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

        // 리더가 참여 신청한 경우
        if (user.equals(study.getLeader())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

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
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        if (request.response()) {
            userStudyLink.approve();
            return UserStudyJoinStatus.APPROVED;
        }
        userStudyLink.reject();

        return UserStudyJoinStatus.REJECTED;
    }

    public List<UserStudyLink> getAllJoinRequest(Long cursor, Long studyId) {
        return userStudyLinkRepository.paginateJoinRequest(cursor, studyId);
    }

    public void deleteByStudyId(Long studyId) {
        userStudyLinkRepository.deleteByStudyId(studyId);
    }

    public List<UserStudyLink> getAllParticipants(Long studyId, Long cursor) {
        return userStudyLinkRepository.paginateParticipants(studyId, cursor);
    }

    public void withdrawJoinStudyRequest(Long studyId, User currentUser) {
        userStudyLinkRepository.deleteByStudyId(studyId, currentUser.getId(), PENDING);
    }

    public void withdrawParticipation(Long studyId, User currentUser) {
        userStudyLinkRepository.deleteByStudyId(studyId, currentUser.getId(), APPROVED);
    }
}
