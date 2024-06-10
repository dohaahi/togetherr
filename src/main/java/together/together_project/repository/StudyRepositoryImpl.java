package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT;
import static together.together_project.domain.QStudy.study;

import com.querydsl.jpa.impl.JPAQueryFactory;
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

    private final JPAQueryFactory q;
    private final StudyJpaRepository studyRepository;

    public Study save(Study study) {
        return studyRepository.save(study);
    }

    public Optional<Study> findById(Long id) {
        return q.select(study)
                .from(study)
                .where(study.studyId.eq(id))
                .where(study.deletedAt.isNull())
                .stream()
                .findFirst();
    }

    public List<Study> paginateStudy(Long cursor) {
        if (studyRepository.findAll().isEmpty()) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        } else if (null == cursor) {
            cursor = q.select(study)
                    .from(study)
                    .orderBy(study.studyId.desc())
                    .fetchOne()
                    .getStudyId() + 1L;
        }

        return q.select(study)
                .from(study)
                .orderBy(study.studyId.desc())
                .where(study.studyId.lt(cursor))
                .limit(PAGINATION_COUNT + 1)
                .fetch();
    }
}
