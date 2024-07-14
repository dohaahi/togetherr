package together.together_project.service;

import static together.together_project.domain.UserStudyJoinStatus.LEADER;
import static together.together_project.validator.StudyValidator.checkMaxPeopleMoreThanMinimum;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPost;
import together.together_project.domain.User;
import together.together_project.domain.UserStudyLink;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.StudyPostRepositoryImpl;
import together.together_project.repository.StudyRepositoryImpl;
import together.together_project.repository.UserStudyLinkRepositoryImpl;
import together.together_project.service.dto.request.StudyPostBumpRequestDto;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;
import together.together_project.service.dto.request.StudyPostUpdateRequestDto;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepositoryImpl studyRepository;
    private final StudyPostRepositoryImpl studyPostRepository;
    private final UserStudyLinkRepositoryImpl userStudyLinkRepository;

    public Study createStudyPost(StudyPostCreateRequestDto request, User user) {
        checkMaxPeopleMoreThanMinimum(request.maxPeople());
        StudyPost studyPost = request.toStudyPost();
        Study study = request.toStudy(user, studyPost);
        study.increaseParticipantCount();

        studyPostRepository.save(studyPost);
        userStudyLinkRepository.save(UserStudyLink.toUserStudyLinkLeader(study, user, LEADER));

        return studyRepository.save(study);
    }

    public List<Study> getAllStudy(Long cursor) {
        return studyRepository.paginateStudy(cursor);
    }

    public Study getById(Long id) {
        return studyRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
    }

    public Study updateStudyPost(Long id, StudyPostUpdateRequestDto request, User user) {
        verifyUserIsStudyLeader(user, id);

        Study study = studyRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));

        return study.update(request);
    }

    public Study bumpStudyPost(Long id, StudyPostBumpRequestDto request, User user) {
        verifyUserIsStudyLeader(user, id);

        Study study = studyRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));

        study.getStudyPost().bumpStudyPost(request);

        return study;
    }

    public void deleteStudy(Long id, User user) {
        verifyUserIsStudyLeader(user, id);

        Study study = studyRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));

        study.softDelete();
        study.getStudyPost().softDelete();
    }

    private void verifyUserIsStudyLeader(User user, Long studyId) {
        if (!user.getId().equals(getById(studyId).getLeader().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}
