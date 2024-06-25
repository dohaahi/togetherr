package together.together_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.repository.ReviewPostRepositoryImpl;
import together.together_project.service.dto.request.ReviewCreateRequestDto;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewPostService {

    private final StudyService studyService;

    private final ReviewPostRepositoryImpl reviewPostRepository;

    public ReviewPost write(ReviewCreateRequestDto request, User user) {
        Study study = studyService.getById(request.studyId());

        ReviewPost review = ReviewPost.builder()
                .author(user)
                .study(study)
                .content(request.content())
                .reviewPicUrl(request.reviewPicUrl())
                .build();

        return reviewPostRepository.save(review);
    }
}
