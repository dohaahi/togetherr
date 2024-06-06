package together.together_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPostComment;
import together.together_project.domain.User;
import together.together_project.repository.StudyPostCommentRepositoryImpl;
import together.together_project.service.dto.request.WriteCommentRequestDto;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyCommentService {

    private final StudyService studyService;
    private final StudyPostCommentRepositoryImpl studyPostCommentRepository;

    public StudyPostComment write(WriteCommentRequestDto request, Long studyId, User currentUser) {
        Study study = studyService.getById(studyId);

        StudyPostComment comment = StudyPostComment.builder()
                .studyPost(study.getStudyPost())
                .author(currentUser)
                .content(request.content())
                .build();

        return studyPostCommentRepository.save(comment);
    }
}
