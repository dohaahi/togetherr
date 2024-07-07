package together.together_project.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.StudyPostComment;
import together.together_project.domain.StudyPostCommentLikeLink;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.StudyPostCommentLikeLinkRepositoryImpl;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyCommentLikeService {

    private final StudyService studyService;
    private final StudyCommentService studyCommentService;

    private final StudyPostCommentLikeLinkRepositoryImpl studyPostCommentLikeLinkRepository;

    public StudyPostCommentLikeLink like(Long studyId, Long commentId, User currentUser) {
        studyService.getById(studyId);
        StudyPostComment comment = studyCommentService.getCommentById(commentId)
                .like();

        studyPostCommentLikeLinkRepository.findCommentLike(commentId, currentUser.getId())
                .ifPresent(studyCommentLikeLink -> {
                    throw new CustomException(ErrorCode.INVALID_REQUEST);
                });

        StudyPostCommentLikeLink commentLike = StudyPostCommentLikeLink.builder()
                .user(currentUser)
                .studyPostComment(comment)
                .build();

        return studyPostCommentLikeLinkRepository.save(commentLike);
    }

    public List<StudyPostCommentLikeLink> getAllCommentLike(Long studyId, Long studyCommentId, Long cursor) {
        studyService.getById(studyId);

        return studyPostCommentLikeLinkRepository.paginateCommentLike(studyCommentId, cursor);
    }
}
