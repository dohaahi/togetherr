package together.together_project.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.Study;

@Repository
@RequiredArgsConstructor
public class StudyRepositoryImpl {

    private final StudyJpaRepository studyRepository;

    public void save(Study study) {
        studyRepository.save(study);
    }
}
