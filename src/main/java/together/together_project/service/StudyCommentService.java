package together.together_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPostComment;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.StudyPostCommentRepositoryImpl;
import together.together_project.service.dto.request.CommentUpdateRequestDto;
import together.together_project.service.dto.request.CommentWriteRequestDto;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyCommentService {

    private final StudyService studyService;
    private final StudyPostCommentRepositoryImpl studyPostCommentRepository;

    public StudyPostComment write(CommentWriteRequestDto request, Long studyId, User currentUser) {
        Study study = studyService.getById(studyId);

        StudyPostComment comment = StudyPostComment.builder()
                .studyPost(study.getStudyPost())
                .author(currentUser)
                .content(request.content())
                .build();

        return studyPostCommentRepository.save(comment);
    }

    public StudyPostComment getCommentById(Long commentId) {
        return studyPostCommentRepository.findCommentById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public StudyPostComment updateComment(
            Long studyId,
            Long commentId,
            CommentUpdateRequestDto request,
            User currentUser
    ) {
        Study study = studyService.getById(studyId);
        StudyPostComment comment = getCommentById(commentId);
        comment.update(request);

        return comment;
    }

    public void withdrawComment(Long commentId) {
        StudyPostComment comment = getCommentById(commentId);
        comment.softDelete();
    }

    public StudyPostComment writeChild(Long studyId, Long commentId, CommentWriteRequestDto request, User currentUser) {
        Study study = studyService.getById(studyId);
        checkParentCommentDeleted(commentId);

        StudyPostComment comment = StudyPostComment.builder()
                .studyPost(study.getStudyPost())
                .author(currentUser)
                .content(request.content())
                .parentCommentId(commentId)
                .build();

        return studyPostCommentRepository.save(comment);
    }

    public StudyPostComment updateChildComment(
            Long studyId,
            Long parentCommentId,
            Long childCommentId,
            CommentUpdateRequestDto request
    ) {
        Study study = studyService.getById(studyId);
        checkParentCommentDeleted(parentCommentId);

        StudyPostComment comment = getCommentById(childCommentId);
        comment.update(request);

        return comment;
    }

    private void checkParentCommentDeleted(Long commentId) {
        studyPostCommentRepository.findCommentById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_ALREADY_DELETED));
    }
}
