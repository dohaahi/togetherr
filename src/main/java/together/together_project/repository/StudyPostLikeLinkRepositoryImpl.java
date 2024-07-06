package together.together_project.repository;

import static together.together_project.domain.QStudyPostLikeLink.studyPostLikeLink;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.StudyPostLikeLink;

@Repository
@RequiredArgsConstructor
public class StudyPostLikeLinkRepositoryImpl {

    private final JPAQueryFactory q;
    private final StudyPostLikeLinkJpaRepository studyPostLikeLinkRepository;

    public StudyPostLikeLink save(StudyPostLikeLink studyPostLikeLink) {
        return studyPostLikeLinkRepository.save(studyPostLikeLink);
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
}
