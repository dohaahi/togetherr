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

    public StudyPostComment updateComment(
            Long studyId,
            Long commentId,
            CommentUpdateRequestDto request,
            User currentUser
    ) {
        Study study = studyService.getById(studyId);
        StudyPostComment comment = studyPostCommentRepository.findCommentById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.update(request);

        return comment;
    }
}
