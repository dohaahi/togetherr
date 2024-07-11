package together.together_project.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.StudyPostComment;
import together.together_project.domain.StudyPostCommentLikeLink;
import together.together_project.domain.User;
import together.together_project.repository.StudyPostCommentLikeLinkRepositoryImpl;
import together.together_project.service.dto.response.StudyCommentLikeLinkResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyCommentLikeService {

    private final StudyService studyService;
    private final StudyCommentService studyCommentService;

    private final StudyPostCommentLikeLinkRepositoryImpl studyPostCommentLikeLinkRepository;

    public StudyCommentLikeLinkResponse like(Long studyId, Long commentId, User user) {
        studyService.getById(studyId);

        StudyPostComment comment = studyCommentService.getCommentById(commentId);

        Optional<StudyPostCommentLikeLink> commentLikeLink = studyPostCommentLikeLinkRepository.findCommentLike(
                commentId,
                user.getId());

        if (commentLikeLink.isEmpty()) {
            StudyPostCommentLikeLink commentLike = StudyPostCommentLikeLink.builder()
                    .user(user)
                    .studyPostComment(comment)
                    .build();

            comment.like();
            studyPostCommentLikeLinkRepository.save(commentLike);
            return StudyCommentLikeLinkResponse.of(commentLike, true);
        }

        return withdrawCommentLike(commentId, commentLikeLink.get());
    }

    public List<StudyPostCommentLikeLink> getAllCommentLike(Long studyId, Long studyCommentId, Long cursor) {
        studyService.getById(studyId);

        return studyPostCommentLikeLinkRepository.paginateCommentLike(studyCommentId, cursor);
    }

    public StudyCommentLikeLinkResponse withdrawCommentLike(Long commentId,
                                                            StudyPostCommentLikeLink commentLike
    ) {
        studyCommentService.getCommentById(commentId)
                .unlike();
        studyPostCommentLikeLinkRepository.deleteComment(commentLike.getId());

        return StudyCommentLikeLinkResponse.of(commentLike, false);
    }
}
