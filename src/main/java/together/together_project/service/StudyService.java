package together.together_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPost;
import together.together_project.domain.User;
import together.together_project.repository.StudyPostRepositoryImpl;
import together.together_project.repository.StudyRepositoryImpl;
import together.together_project.service.dto.request.StudiesRequestDto;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepositoryImpl studyRepository;
    private final StudyPostRepositoryImpl studyPostRepository;

    public Study createStudyPost(StudiesRequestDto request, User user) {
        StudyPost studyPost = request.toStudyPost();
        Study study = request.toStudy(user, studyPost);

        studyPostRepository.save(studyPost);
        studyRepository.save(study);

        return study;
    }
}
