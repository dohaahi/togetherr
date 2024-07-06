package together.together_project.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.StudyPost;
import together.together_project.domain.StudyPostLikeLink;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.StudyPostLikeLinkRepositoryImpl;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyPostLikeService {

    private final StudyService studyService;

    private final StudyPostLikeLinkRepositoryImpl studyPostLikeLinkRepository;

    public StudyPostLikeLink like(Long studyId, User user) {
        StudyPost studyPost = studyService.getById(studyId)
                .getStudyPost()
                .like();

        studyPostLikeLinkRepository.findStudyPostLikeLink(studyId, user.getId())
                .ifPresent(studyPostLikeLink -> {
                    throw new CustomException(ErrorCode.INVALID_REQUEST);
                });

        StudyPostLikeLink studyPostLikeLink = StudyPostLikeLink.builder()
                .studyPost(studyPost)
                .user(user)
                .build();

        return studyPostLikeLinkRepository.save(studyPostLikeLink);
    }

    public List<StudyPostLikeLink> getStudyLike(Long studyId, Long cursor) {
        studyService.getById(studyId);

        return studyPostLikeLinkRepository.paginateStudyLike(studyId, cursor);
    }
}
