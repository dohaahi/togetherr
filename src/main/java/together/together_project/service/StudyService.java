package together.together_project.service;

import static together.together_project.validator.StudyValidator.checkMaxPeopleMoreThanMinimum;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPost;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.StudyPostRepositoryImpl;
import together.together_project.repository.StudyRepositoryImpl;
import together.together_project.service.dto.PaginationRequestDto;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepositoryImpl studyRepository;
    private final StudyPostRepositoryImpl studyPostRepository;

    public Study createStudyPost(StudyPostCreateRequestDto request, User user) {
        checkMaxPeopleMoreThanMinimum(request);
        StudyPost studyPost = request.toStudyPost();
        Study study = request.toStudy(user, studyPost);

        studyPostRepository.save(studyPost);
        return studyRepository.save(study);
    }

    public List<Study> getAllStudy(PaginationRequestDto request) {
        return studyRepository.paginateStudy(request.after(), request.count());
    }

    public Study getById(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_POST_NOT_FOUND));
    }
}
