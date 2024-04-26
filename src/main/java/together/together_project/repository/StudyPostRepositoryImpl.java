package together.together_project.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudyPostRepositoryImpl {

    private final StudyPostJpaRepository studyPostRepository;
}
