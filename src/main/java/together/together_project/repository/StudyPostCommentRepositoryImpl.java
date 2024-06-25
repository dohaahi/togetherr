package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT;
import static together.together_project.domain.QStudyPostComment.studyPostComment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.StudyPostComment;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

@Repository
@RequiredArgsConstructor
public class StudyPostCommentRepositoryImpl {

    private final JPAQueryFactory q;
    private final StudyPostCommentJpaRepository studyPostCommentRepository;

    public StudyPostComment save(StudyPostComment comment) {
        return studyPostCommentRepository.save(comment);
    }

    public Optional<StudyPostComment> findCommentById(Long commentId) {
        return q.select(studyPostComment)
                .from(studyPostComment)
                .where(studyPostComment.deletedAt.isNull())
                .where(studyPostComment.id.eq(commentId))
                .stream()
                .findFirst();
    }

    public List<StudyPostComment> paginateComment(Long studyId, Long cursor) {
        if (null == cursor) {
            cursor = q.select(studyPostComment)
                    .from(studyPostComment)
                    .orderBy(studyPostComment.id.desc())
                    .where(studyPostComment.studyPost.studyPostId.eq(studyId)
                            .and(studyPostComment.deletedAt.isNull()))
                    .fetchFirst()
                    .getId() + 1L;
        }

        List<StudyPostComment> comments = q.select(studyPostComment)
                .from(studyPostComment)
                .orderBy(studyPostComment.id.desc())
                .where(studyPostComment.id.lt(cursor)
                        .and(studyPostComment.studyPost.studyPostId.eq(studyId))
                        .and(studyPostComment.deletedAt.isNull())
                        .and(studyPostComment.parentCommentId.isNull()))
                .limit(PAGINATION_COUNT + 1)
                .fetch();

        if (comments.isEmpty()) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return comments;
    }

    public List<StudyPostComment> paginateChildComment(Long studyId, Long parentCommentId, Long cursor) {
        if (null == cursor) {
            StudyPostComment comment = q.select(studyPostComment)
                    .from(studyPostComment)
                    .orderBy(studyPostComment.id.desc())
                    .where(studyPostComment.deletedAt.isNull()
                            .and(studyPostComment.studyPost.studyPostId.eq(studyId))
                            .and(studyPostComment.parentCommentId.eq(parentCommentId)))
                    .fetchFirst();

            if (comment == null) {
                throw new CustomException(ErrorCode.DATA_NOT_FOUND);
            }

            cursor = comment.getId() + 1L;
        }

        List<StudyPostComment> comments = q.select(studyPostComment)
                .from(studyPostComment)
                .orderBy(studyPostComment.id.desc())
                .where(studyPostComment.id.lt(cursor)
                        .and(studyPostComment.deletedAt.isNull())
                        .and(studyPostComment.studyPost.studyPostId.eq(studyId))
                        .and(studyPostComment.parentCommentId.eq(parentCommentId)))
                .limit(PAGINATION_COUNT_AND_ONE_MORE)
                .fetch();

        if (comments.isEmpty()) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return comments;
    }
}
