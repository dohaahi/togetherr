package together.together_project.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.StudyPost;

@Repository
@RequiredArgsConstructor
public class StudyPostRepositoryImpl {

    private final StudyPostJpaRepository studyPostRepository;

    public void save(StudyPost studyPost) {
        studyPostRepository.save(studyPost);
    }
}
