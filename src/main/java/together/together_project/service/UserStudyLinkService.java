package together.together_project.service;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.domain.UserStudyLink;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.UserStudyLinkRepositoryImpl;

@Service
@Transactional
@RequiredArgsConstructor
public class UserStudyLinkService {

    private final StudyService studyService;
    private final UserStudyLinkRepositoryImpl userStudyLinkRepository;

    private final EntityManager em;

    public void join(Long studyId, User user) {
        Study study = studyService.getById(studyId);

        // 이미 참여 신청한 유저인지 확인
        Optional<UserStudyLink> studyLink = userStudyLinkRepository.findByStudyId(study.getStudyId());
        if (studyLink.isPresent() && studyLink.get().getParticipant() == user) {
            throw new CustomException(ErrorCode.STUDY_ALREADY_JOINED);
        }

        UserStudyLink userStudyLink = UserStudyLink.toUserStudyLink(study, user);

        userStudyLinkRepository.save(userStudyLink);
        userStudyLink.approve();
    }
}
