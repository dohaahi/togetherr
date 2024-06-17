package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT;
import static together.together_project.domain.QUserStudyLink.userStudyLink;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.UserStudyJoinStatus;
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
                .where(userStudyLink.study.studyId.eq(studyId)
                        .and(userStudyLink.participant.id.eq(userId))
                        .and(userStudyLink.deletedAt.isNull()))
                .stream()
                .findFirst();
    }

    public List<UserStudyLink> paginateUserStudyLink(Long studyId, Long cursor, UserStudyJoinStatus status) {
        JPAQuery<UserStudyLink> userStudyLinks = q.select(userStudyLink)
                .from(userStudyLink)
                .where(userStudyLink.study.studyId.eq(studyId)
                        .and(userStudyLink.status.eq(status)));

        if (userStudyLinks.fetch().isEmpty()) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        } else if (null == cursor) {
            cursor = userStudyLinks
                    .where(userStudyLink.deletedAt.isNull())
                    .orderBy(userStudyLink.id.desc())
                    .limit(1)
                    .fetchOne()
                    .getId() + 1;
        }

        return userStudyLinks
                .where(userStudyLink.id.lt(cursor)
                        .and(userStudyLink.deletedAt.isNull()))
                .limit(PAGINATION_COUNT + 1)
                .fetch();
    }

    public void findByStudyId(Long studyId) {
        q.delete(userStudyLink)
                .where(userStudyLink.study.studyId.eq(studyId))
                .execute();
    }

    public Optional<UserStudyLink> findByStudyId(Long studyId, Long userId, UserStudyJoinStatus status) {
        return q.select(userStudyLink)
                .from(userStudyLink)
                .where(userStudyLink.study.studyId.eq(studyId)
                        .and(userStudyLink.participant.id.eq(userId))
                        .and(userStudyLink.status.eq(status))
                        .and(userStudyLink.deletedAt.isNull()))
                .stream()
                .findFirst();
    }

    public List<UserStudyLink> findByUserId(Long userId) {
        return q.select(userStudyLink)
                .from(userStudyLink)
                .where(userStudyLink.participant.id.eq(userId)
                        .and(userStudyLink.deletedAt.isNull()))
                .fetch();
    }
}
