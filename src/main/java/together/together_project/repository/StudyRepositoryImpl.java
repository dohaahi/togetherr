package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.Study;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

@Repository
@RequiredArgsConstructor
public class StudyRepositoryImpl {

    private final StudyJpaRepository studyRepository;

    public Study save(Study study) {
        return studyRepository.save(study);
    }

    public List<Study> paginateStudy(Long cursor) {
        if (studyRepository.findAll().isEmpty()) {
            throw new CustomException(ErrorCode.STUDY_NOT_FOUND);
        } else if (null == cursor) {
            cursor = studyRepository.findFirstByOrderByIdDesc() + 1;
        }

        return studyRepository.paginateStudy(cursor, (long) (PAGINATION_COUNT + 1));
    }

    public Optional<Study> findById(Long id) {
        return studyRepository.findById(id);
    }
}
