package together.together_project.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.Study;

@Repository
@RequiredArgsConstructor
public class StudyRepositoryImpl {

    private final StudyJpaRepository studyRepository;

    public Study save(Study study) {
        return studyRepository.save(study);
    }

    public List<Study> paginateStudy(Long after, Long count) {
        if (studyRepository.findAll().isEmpty()) {
            after = 0L;
        } else {
            after = after == null ? studyRepository.findFirstByOrderByIdDesc() + 1 : after;
        }

        return studyRepository.paginateStudy(after, count + 1);
    }

    public Optional<Study> findById(Long id) {
        return studyRepository.findById(id);
    }
}
