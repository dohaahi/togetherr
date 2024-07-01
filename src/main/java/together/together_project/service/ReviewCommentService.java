package together.together_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.controller.ReviewCommentCreateRequestDto;
import together.together_project.domain.ReviewComment;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.ReviewCommentRepositoryImpl;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommentService {

    private final ReviewPostService reviewPostService;

    private final ReviewCommentRepositoryImpl reviewCommentRepository;

    public ReviewComment writeComment(Long reviewId, ReviewCommentCreateRequestDto request, User user) {
        ReviewPost review = reviewPostService.getReview(reviewId);

        if (request.content().trim().isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_CONTENT_ERROR);
        }
        
        ReviewComment comment = ReviewComment.builder()
                .author(user)
                .reviewPost(review)
                .content(request.content())
                .build();

        return reviewCommentRepository.save(comment);
    }
}
