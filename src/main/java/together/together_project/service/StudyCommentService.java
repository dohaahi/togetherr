package together.together_project.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.BaseTimeEntity;
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
        studyPostCommentRepository.findCommentByIdWithChildComment(commentId)
                .forEach(BaseTimeEntity::softDelete);
    }

    public void withdrawCommentWithStudy(Long studyId) {
        studyPostCommentRepository.findCommentByStudyId(studyId)
                .forEach(BaseTimeEntity::softDelete);
    }

    public StudyPostComment writeChildComment(
            Long studyId,
            Long commentId,
            CommentWriteRequestDto request,
            User currentUser
    ) {
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

        // TODO: child일 때만 수정, 삭제 되도록 수정
        StudyPostComment comment = studyPostCommentRepository.findCommentById(childCommentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_ALREADY_DELETED));
        comment.update(request);

        return comment;
    }

    public void withdrawChildComment(Long studyId, Long parentCommentId, Long childCommentId) {
        studyService.getById(studyId);
        checkParentCommentDeleted(parentCommentId);

        studyPostCommentRepository.findCommentById(childCommentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND))
                .softDelete();
    }

    public List<StudyPostComment> getAllComment(Long studyId, Long cursor) {
        studyService.getById(studyId);
        return studyPostCommentRepository.paginateComment(studyId, cursor);
    }

    private void checkParentCommentDeleted(Long commentId) {
        studyPostCommentRepository.findCommentById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_ALREADY_DELETED));
    }

    public List<StudyPostComment> getChildComment(Long studyId, Long parentCommentId, Long cursor) {
        studyService.getById(studyId);
        getCommentById(parentCommentId);
        return studyPostCommentRepository.paginateChildComment(studyId, parentCommentId, cursor);
    }
}
