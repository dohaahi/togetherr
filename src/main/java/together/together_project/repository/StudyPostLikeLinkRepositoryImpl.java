package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT_AND_ONE_MORE;
import static together.together_project.domain.QStudyPostLikeLink.studyPostLikeLink;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.StudyPostLikeLink;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

@Repository
@RequiredArgsConstructor
public class StudyPostLikeLinkRepositoryImpl {

    private final JPAQueryFactory q;
    private final StudyPostLikeLinkJpaRepository studyPostLikeLinkRepository;

    public StudyPostLikeLink save(StudyPostLikeLink studyPostLikeLink) {
        return studyPostLikeLinkRepository.save(studyPostLikeLink);
    }

    public Optional<StudyPostLikeLink> findStudyPostLikeLink(Long studyLikeLinkId) {
        return q.select(studyPostLikeLink)
                .from(studyPostLikeLink)
                .where(studyPostLikeLink.id.eq(studyLikeLinkId)
                        .and(studyPostLikeLink.deletedAt.isNull()))
                .stream()
                .findFirst();
    }

    public Optional<StudyPostLikeLink> findStudyPostLikeLink(Long studyId, Long userId) {
        return q.select(studyPostLikeLink)
                .from(studyPostLikeLink)
                .where(studyPostLikeLink.studyPost.studyPostId.eq(studyId)
                        .and(studyPostLikeLink.user.id.eq(userId))
                        .and(studyPostLikeLink.deletedAt.isNull()))
                .stream()
                .findFirst();
    }

    public List<StudyPostLikeLink> paginateStudyLike(Long studyId, Long cursor) {
        if (cursor == null) {
            StudyPostLikeLink likeLink = q.select(studyPostLikeLink)
                    .from(studyPostLikeLink)
                    .orderBy(studyPostLikeLink.id.desc())
                    .where(studyPostLikeLink.studyPost.studyPostId.eq(studyId)
                            .and(studyPostLikeLink.deletedAt.isNull()))
                    .fetchFirst();

            if (likeLink == null) {
                throw new CustomException(ErrorCode.DATA_NOT_FOUND);
            }

            cursor = likeLink.getId() + 1;
        }

        List<StudyPostLikeLink> likeLinks = q.select(studyPostLikeLink)
                .from(studyPostLikeLink)
                .orderBy(studyPostLikeLink.id.desc())
                .where(studyPostLikeLink.studyPost.studyPostId.eq(studyId)
                        .and(studyPostLikeLink.id.lt(cursor))
                        .and(studyPostLikeLink.deletedAt.isNull()))
                .limit(PAGINATION_COUNT_AND_ONE_MORE)
                .fetch();

        if (likeLinks == null) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return likeLinks;
    }
}
