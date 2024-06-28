package together.together_project.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.ReviewPostRepositoryImpl;
import together.together_project.service.dto.request.ReviewCreateRequestDto;
import together.together_project.service.dto.request.ReviewUpdateRequestDto;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewPostService {

    private final StudyService studyService;
    private final UserStudyLinkService userStudyLinkService;

    private final ReviewPostRepositoryImpl reviewPostRepository;

    public ReviewPost write(ReviewCreateRequestDto request, User user) {
        Study study = studyService.getById(request.studyId());

        if (!study.getLeader().equals(user)) {
            userStudyLinkService.checkUserParticipant(request.studyId(), user.getId());
        }

        ReviewPost review = ReviewPost.builder().author(user).study(study).content(request.content())
                .reviewPicUrl(request.reviewPicUrl()).build();

        return reviewPostRepository.save(review);
    }

    public ReviewPost updateReview(Long reviewId, ReviewUpdateRequestDto request) {
        studyService.getById(request.studyId());

        return getReview(reviewId)
                .update(request);
    }

    public void withdrawReview(Long reviewId) {
        ReviewPost reviewPost = reviewPostRepository.findReviewByReviewId(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        studyService.getById(reviewPost.getStudy().getStudyId());

        reviewPost.softDelete();
    }

    public ReviewPost getReview(Long reviewId) {
        return reviewPostRepository.findReviewByReviewId(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

    }

    public List<ReviewPost> getAllReview(Long cursor) {
        return reviewPostRepository.paginateReviews(cursor);
    }
}
