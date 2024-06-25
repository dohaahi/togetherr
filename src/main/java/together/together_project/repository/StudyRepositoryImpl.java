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
                .where(study.studyId.eq(id)
                        .and(study.deletedAt.isNull()))
                .stream()
                .findFirst();
    }

    public List<Study> paginateStudy(Long cursor) {
        if (null == cursor) {
            cursor = q.select(study)
                    .from(study)
                    .where(study.deletedAt.isNull())
                    .orderBy(study.studyId.desc())
                    .fetchFirst()
                    .getStudyId() + 1L;
        }

        List<Study> studies = q.select(study)
                .from(study)
                .orderBy(study.studyId.desc())
                .where(study.studyId.lt(cursor)
                        .and(study.deletedAt.isNull()))
                .limit(PAGINATION_COUNT + 1)
                .fetch();

        if (studies.isEmpty()) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return studies;
    }
}
