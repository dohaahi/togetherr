package together.together_project.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.StudyPost;
import together.together_project.domain.StudyPostLikeLink;
import together.together_project.domain.User;
import together.together_project.repository.StudyPostLikeLinkRepositoryImpl;
import together.together_project.service.dto.response.StudyPostLikeResponseDto;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyPostLikeService {

    private static final boolean LIKE = true;
    private static final boolean UNLIKE = false;

    private final StudyService studyService;

    private final StudyPostLikeLinkRepositoryImpl studyPostLikeLinkRepository;

    public StudyPostLikeResponseDto like(Long studyId, User user) {
        StudyPost studyPost = studyService.getById(studyId)
                .getStudyPost();

        Optional<StudyPostLikeLink> studyPostLikeLink = studyPostLikeLinkRepository.findStudyPostLikeLink(studyId,
                user.getId());

        if (studyPostLikeLink.isEmpty()) {
            StudyPostLikeLink studyLike = StudyPostLikeLink.builder()
                    .studyPost(studyPost)
                    .user(user)
                    .build();

            studyPost.like();
            studyPostLikeLinkRepository.save(studyLike);
            return StudyPostLikeResponseDto.of(studyLike, LIKE);
        }

        return withdrawStudyLike(studyId, studyPostLikeLink.get());
    }

    public List<StudyPostLikeLink> getStudyLike(Long studyId, Long cursor) {
        studyService.getById(studyId);

        return studyPostLikeLinkRepository.paginateStudyLike(studyId, cursor);
    }

    public StudyPostLikeResponseDto withdrawStudyLike(Long studyId, StudyPostLikeLink studyPostLikeLink) {
        studyService.getById(studyId)
                .getStudyPost()
                .unlike();
        studyPostLikeLinkRepository.delete(studyPostLikeLink.getId());

        return StudyPostLikeResponseDto.of(studyPostLikeLink, UNLIKE);
    }
}
