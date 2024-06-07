package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT;
import static together.together_project.domain.QUserStudyLink.userStudyLink;
import static together.together_project.domain.UserStudyJoinStatus.APPROVED;
import static together.together_project.domain.UserStudyJoinStatus.PENDING;

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
                .where(userStudyLink.study.studyId.eq(studyId))
                .where(userStudyLink.participant.id.eq(userId))
                .stream()
                .findFirst();
    }

    public List<UserStudyLink> paginateJoinRequest(Long cursor, Long studyId) {
        JPAQuery<UserStudyLink> userStudyLinks = getUserStudyLinks(studyId, PENDING);

        if (userStudyLinks.fetch().isEmpty()) {
            throw new CustomException(ErrorCode.JOIN_REQUEST_NOT_FOUND);
        } else if (null == cursor) {
            cursor = userStudyLinks
                    .orderBy(userStudyLink.id.desc())
                    .limit(1)
                    .fetchOne()
                    .getId() + 1;
        }

        return userStudyLinks
                .where(userStudyLink.id.lt(cursor))
                .limit(PAGINATION_COUNT + 1)
                .fetch();
    }

    public List<UserStudyLink> paginateParticipants(Long studyId, Long cursor) {
        JPAQuery<UserStudyLink> userStudyLinks = getUserStudyLinks(studyId, APPROVED);

        if (userStudyLinks.fetch().isEmpty()) {
            throw new CustomException(ErrorCode.PARTICIPANTS_NOT_FOUND);
        } else if (cursor == null) {
            cursor = userStudyLinks
                    .orderBy(userStudyLink.id.desc())
                    .limit(1)
                    .fetchOne()
                    .getId() + 1;
        }

        return userStudyLinks
                .where(userStudyLink.id.lt(cursor))
                .orderBy(userStudyLink.id.desc())
                .limit(PAGINATION_COUNT + 1)
                .fetch();
    }

    private JPAQuery<UserStudyLink> getUserStudyLinks(Long studyId, UserStudyJoinStatus status) {
        return q.select(userStudyLink)
                .from(userStudyLink)
                .where(userStudyLink.study.studyId.eq(studyId))
                .where(userStudyLink.status.eq(status));
    }

    public void deleteByStudyId(Long studyId) {
        q.delete(userStudyLink)
                .where(userStudyLink.study.studyId.eq(studyId))
                .execute();
    }

    public void deleteByStudyId(Long studyId, Long userId, UserStudyJoinStatus status) {
        long affectedRows = q.delete(userStudyLink)
                .where(userStudyLink.study.studyId.eq(studyId))
                .where(userStudyLink.participant.id.eq(userId))
                .where(userStudyLink.status.eq(status))
                .execute();

        if (affectedRows == 0) {
            // NOTE - 참여 신청하지 않은 유저가 철회하는 경우, 리더가 참여 신청 철회하는 경우 등에도 해당 메세지가 쓰임.. 메세지 변경 필요
            throw new CustomException(ErrorCode.ALREADY_WITHDRAW_JOIN_REQUEST);
        }
    }
}
