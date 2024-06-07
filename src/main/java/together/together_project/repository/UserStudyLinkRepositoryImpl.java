package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT;
import static together.together_project.domain.QUserStudyLink.userStudyLink;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.UserStudyLink;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

@Repository
@RequiredArgsConstructor
public class UserStudyLinkRepositoryImpl {

    private final JPAQueryFactory q;
    private final UserStudyLinkJpaRepository userStudyLinkRepository;

    public void save(UserStudyLink userStudyLink) {
        userStudyLinkRepository.save(userStudyLink);
    }

    public Optional<UserStudyLink> findByStudyIdAndUserId(Long studyId, Long userId) {
        return q.select(userStudyLink)
                .from(userStudyLink)
                .where(userStudyLink.study.studyId.eq(studyId))
                .where(userStudyLink.participant.id.eq(userId))
                .stream()
                .findFirst();
    }

    public List<UserStudyLink> paginateJoinRequest(Long cursor, Long studyId) {
        if (userStudyLinkRepository.findAll().isEmpty()) {
            throw new CustomException(ErrorCode.PARTICIPANTS_NOT_FOUND);
        } else if (null == cursor) {
            cursor = q.select(userStudyLink)
                    .from(userStudyLink)
                    .orderBy(userStudyLink.id.desc())
                    .fetchOne()
                    .getId() + 1;
        }

        return q.select(userStudyLink)
                .from(userStudyLink)
                .where(userStudyLink.study.studyId.eq(studyId))
                .where(userStudyLink.id.lt(cursor))
                .limit(PAGINATION_COUNT + 1)
                .fetch();
    }

    public void deleteByStudyId(Long studyId) {
        q.delete(userStudyLink)
                .where(userStudyLink.study.studyId.eq(studyId))
                .execute();
    }
}
