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
    private final UserStudyLikeService userStudyLikeService;

    private final ReviewPostRepositoryImpl reviewPostRepository;

    public ReviewPost write(ReviewCreateRequestDto request, User user) {
        Study study = studyService.getById(request.studyId());

        // 하나의 스터디 당 리뷰는 한 개만 작성 가능
        reviewPostRepository.findReviewByStudyAndUser(request.studyId(), user.getId())
                .ifPresent(reviewPost -> {
                    throw new CustomException(ErrorCode.REVIEW_DUPLICATE);
                });

        if (!study.getLeader().equals(user)) {
            userStudyLikeService.checkUserParticipant(request.studyId(), user.getId());
        }

        ReviewPost review = request.toReviewPost(study, user);

        return reviewPostRepository.save(review);
    }

    public ReviewPost updateReview(Long reviewId, ReviewUpdateRequestDto request, User user) {
        Study study = null;
        if (request.studyId() != null) {
            userStudyLikeService.checkUserParticipant(request.studyId(), user.getId());
            study = studyService.getById(request.studyId());
        }

        return getReview(reviewId)
                .update(request, study);
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

    public List<ReviewPost> getAllReviews(Long userId, Long cursor) {
        return reviewPostRepository.paginateReviews(userId, cursor);
    }
}
