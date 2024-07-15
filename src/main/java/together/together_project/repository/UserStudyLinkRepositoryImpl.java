package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT_AND_ONE_MORE;
import static together.together_project.domain.QUserStudyLink.userStudyLink;
import static together.together_project.domain.UserStudyJoinStatus.APPROVED;
import static together.together_project.domain.UserStudyJoinStatus.LEADER;

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
        if (null == cursor) {
            UserStudyLink studyLink = q.select(userStudyLink)
                    .from(userStudyLink)
                    .orderBy(userStudyLink.id.desc())
                    .where(userStudyLink.deletedAt.isNull()
                            .and(userStudyLink.study.studyId.eq(studyId))
                            .and(userStudyLink.status.eq(status)))
                    .fetchFirst();

            if (studyLink == null) {
                throw new CustomException(ErrorCode.DATA_NOT_FOUND);
            }

            cursor = studyLink.getId() + 1;
        }

        List<UserStudyLink> studyLinks = q.select(userStudyLink)
                .from(userStudyLink)
                .orderBy(userStudyLink.id.desc())
                .where(userStudyLink.id.lt(cursor)
                        .and(userStudyLink.deletedAt.isNull())
                        .and(userStudyLink.study.studyId.eq(studyId))
                        .and(userStudyLink.status.eq(status)))
                .limit(PAGINATION_COUNT_AND_ONE_MORE)
                .fetch();

        if (studyLinks.isEmpty()) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return studyLinks;
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

    public List<UserStudyLink> findPaginateAllParticipatingStudy(Long userId, Long cursor) {
        if (cursor == null) {
            UserStudyLink studyLink = q.select(userStudyLink)
                    .from(userStudyLink)
                    .orderBy(userStudyLink.id.desc())
                    .where(userStudyLink.participant.id.eq(userId)
                            .and(userStudyLink.deletedAt.isNull())
                            .and(userStudyLink.status.eq(APPROVED)
                                    .or(userStudyLink.status.eq(LEADER))))
                    .fetchFirst();

            if (studyLink == null) {
                throw new CustomException(ErrorCode.DATA_NOT_FOUND);
            }

            cursor = studyLink.getId() + 1;
        }

        List<UserStudyLink> studyLinks = q.select(userStudyLink)
                .from(userStudyLink)
                .orderBy(userStudyLink.id.desc())
                .where(userStudyLink.participant.id.eq(userId)
                        .and(userStudyLink.deletedAt.isNull())
                        .and(userStudyLink.id.lt(cursor))
                        .and(userStudyLink.status.eq(APPROVED)
                                .or(userStudyLink.status.eq(LEADER))))
                .limit(PAGINATION_COUNT_AND_ONE_MORE)
                .fetch();

        if (studyLinks == null) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return studyLinks;
    }

    public List<UserStudyLink> getAll() {
        return q.select(userStudyLink)
                .from(userStudyLink)
                .where(userStudyLink.deletedAt.isNull()
                        .and(userStudyLink.status.ne(LEADER)))
                .fetch();
    }
}
