package together.together_project.service;

import static together.together_project.validator.StudyValidator.verifyCreateStudyPost;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPost;
import together.together_project.domain.User;
import together.together_project.repository.StudyPostRepositoryImpl;
import together.together_project.repository.StudyRepositoryImpl;
import together.together_project.service.dto.PaginationRequestDto;
import together.together_project.service.dto.request.StudiesRequestDto;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepositoryImpl studyRepository;
    private final StudyPostRepositoryImpl studyPostRepository;

    public Study createStudyPost(StudiesRequestDto request, User user) {
        verifyCreateStudyPost(request);
        StudyPost studyPost = request.toStudyPost();
        Study study = request.toStudy(user, studyPost);

        studyPostRepository.save(studyPost);
        return studyRepository.save(study);
    }

    public List<Study> getAllStudy(PaginationRequestDto request) {
        return studyRepository.paginateStudy(request.after(), request.count());
    }
}
