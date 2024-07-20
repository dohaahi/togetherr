package together.together_project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.ReviewPost;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.dto.request.RespondToJoinRequestDto;
import together.together_project.service.dto.request.ReviewCreateRequestDto;
import together.together_project.service.dto.request.ReviewUpdateRequestDto;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;

@SpringBootTest
@Transactional
class ReviewPostServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private StudyService studyService;

    @Autowired
    private UserStudyLikeService userStudyLikeService;

    @Autowired
    private ReviewPostService reviewPostService;

    @BeforeEach
    public void setup() {
        createUser("aaa@google.com", "aaa");
        User leader = getLeader();
        StudyPostCreateRequestDto studyPostCreateRequest = new StudyPostCreateRequestDto("title", "content", "location",
                5);
        Study study = studyService.createStudyPost(studyPostCreateRequest, leader);

        createUser("bbb@google.com", "bbb");
        User user = getUser(1);
        userStudyLikeService.join(study.getStudyId(), user);

        RespondToJoinRequestDto respondToJoinRequest = new RespondToJoinRequestDto(user.getId(), true);
        userStudyLikeService.respondToJoinRequest(respondToJoinRequest, study.getStudyId(), leader);

        reviewPostService.write(new ReviewCreateRequestDto(study.getStudyId(), "review", "url"), user);
    }

    /* NOTE - 리뷰 게시글 생성
        스터디를 선택하지 않은 경우
        내용을 입력하지 않은 경우
        사진을 삽입하지 않은 경우
     */

    @Nested
    class writeReview {

        @DisplayName("리뷰 게시글 생성")
        @Test
        void testWriteReview() {
            User user = getLeader();
            Study study = getStudy(0);
            ReviewCreateRequestDto reviewCreateRequest = new ReviewCreateRequestDto(study.getStudyId(), "content",
                    "url");

            LocalDateTime now = LocalDateTime.now();
            ReviewPost review = reviewPostService.write(reviewCreateRequest, user);

            assertThat(review.getContent()).isEqualTo("content");
            assertThat(review.getReviewPicUrl()).isEqualTo("url");
            assertThat(review.getStudy()).isEqualTo(study);
            assertThat(review.getAuthor()).isEqualTo(user);
            assertThat(review.getTotalLikeCount()).isEqualTo(0);
            assertThat(review.getCreatedAt()).isAfter(now);
            assertThat(review.getUpdatedAt()).isAfter(now);
            assertThat(review.getDeletedAt()).isNull();
        }

        @DisplayName("스터디가 존재하지 않은 경우 예외 발생")
        @Test
        void testWriteReview2() {
            User user = getLeader();
            Study study = getStudy(0);
            ReviewCreateRequestDto reviewCreateRequest = new ReviewCreateRequestDto(study.getStudyId() + 1, "content",
                    "url");

            assertThatThrownBy(() -> reviewPostService.write(reviewCreateRequest, user)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.STUDY_NOT_FOUND.getDescription());
        }

        @DisplayName("참여하지 않은 스터디인 경우 예외 발생")
        @Test
        void testWriteReview3() {
            createUser("ccc@aaa.com", "ccc");
            User user = getUser(2);
            Study study = getStudy(0);

            ReviewCreateRequestDto reviewCreateRequest = new ReviewCreateRequestDto(study.getStudyId(), "content",
                    "url");

            assertThatThrownBy(() -> reviewPostService.write(reviewCreateRequest, user)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("이미 후기를 작성한 경우 예외 발생")
        @Test
        void testWriteReview4() {
            User user = getLeader();
            Study study = getStudy(0);
            ReviewCreateRequestDto reviewCreateRequest = new ReviewCreateRequestDto(study.getStudyId(), "content",
                    "url");
            reviewPostService.write(reviewCreateRequest, user);

            assertThatThrownBy(() -> reviewPostService.write(reviewCreateRequest, user)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.REVIEW_DUPLICATE.getDescription());
        }
    }

    /* NOTE - 리뷰 게시글 수정
        리뷰 작성자가 아닌 경우
        스터디가 존재하지 않은 경우
        리뷰가 존재하지 않은 경우
     */

    @Nested
    class UpdateReview {

        @DisplayName("리뷰 수정")
        @Test
        void updateTest() {
            Study study = getStudy(0);
            ReviewPost reviewPost = reviewPostService.getAllReview(null).get(0);
            User user = getUser(1);

            reviewPostService.updateReview(reviewPost.getId(),
                    new ReviewUpdateRequestDto(study.getStudyId(), "update review", "update url"), user);

            assertThat(reviewPost.getAuthor()).isEqualTo(user);
            assertThat(reviewPost.getStudy()).isEqualTo(study);
            assertThat(reviewPost.getContent()).isEqualTo("update review");
            assertThat(reviewPost.getReviewPicUrl()).isEqualTo("update url");
        }

        @DisplayName("리뷰 작성자가 아닌 경우 예외 발생")
        @Test
        void updateTest2() {
            Study study = getStudy(0);
            ReviewPost reviewPost = reviewPostService.getAllReview(null).get(0);

            assertThatThrownBy(() -> reviewPostService.updateReview(reviewPost.getId(),
                    new ReviewUpdateRequestDto(study.getStudyId(), "update review", "update url"),
                    getLeader())).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("참여중인 스터디가 아닌 경우 예외 발생")
        @Test
        void updateTest3() {
            Study study = getStudy(0);
            ReviewPost reviewPost = reviewPostService.getAllReview(null).get(0);
            studyService.deleteStudy(study.getStudyId(), getLeader());

            assertThatThrownBy(() -> reviewPostService.updateReview(reviewPost.getId(),
                    new ReviewUpdateRequestDto(study.getStudyId(), "update review", "update url"),
                    getUser(1))).isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.STUDY_NOT_FOUND.getDescription());

        }
    }

    @Nested
    class deleteReivew {

        @DisplayName("리뷰 삭제")
        @Test
        void deleteTest() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);

            reviewPostService.withdrawReview(review.getId(), user);

            assertThat(review.getDeletedAt()).isNotNull();
        }

        @DisplayName("리뷰 작성자가 아닌 경우 예외 발생")
        @Test
        void deleteTest2() {
            User leader = getLeader();
            ReviewPost review = reviewPostService.getAllReview(null).get(0);

            assertThatThrownBy(() -> reviewPostService.withdrawReview(review.getId(), leader)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.UNAUTHORIZED_ACCESS.getDescription());
        }

        @DisplayName("리뷰가 존재하지 않는 경우 예외 발생")
        @Test
        void deleteTest3() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            reviewPostService.withdrawReview(review.getId(), user);

            assertThatThrownBy(() -> reviewPostService.withdrawReview(review.getId(), user)).isInstanceOf(
                    CustomException.class).hasMessage(ErrorCode.REVIEW_NOT_FOUND.getDescription());
        }
    }

    @Nested
    class ViewAllReview {

        @DisplayName("리뷰 전체 조회")
        @Test
        void getAllReviewTest() {
            User leader = getLeader();
            Study study = getStudy(0);
            for (int i = 0; i < 3; i++) {
                createUser(i + "email@google.com", "name" + i);
                User user = getUser(2 + i);
                userStudyLikeService.join(study.getStudyId(), user);
                userStudyLikeService.respondToJoinRequest(new RespondToJoinRequestDto(user.getId(), true),
                        study.getStudyId(), leader);
                reviewPostService.write(new ReviewCreateRequestDto(study.getStudyId(), "content" + i, "url" + i), user);
            }

            List<ReviewPost> reviews = reviewPostService.getAllReview(null);

            assertThat(reviews).hasSize(4);
        }

        @DisplayName("리뷰가 없으면 예외 발생")
        @Test
        void getAllReviewTest2() {
            User user = getUser(1);
            ReviewPost review = reviewPostService.getAllReview(null).get(0);
            reviewPostService.withdrawReview(review.getId(), user);

            assertThatThrownBy(() -> reviewPostService.getAllReview(null))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.DATA_NOT_FOUND.getDescription());
        }
    }

    @Nested
    class ViewReview {

        @DisplayName("리뷰 상세 조회")
        @Test
        void getReview() {
            User user = getUser(1);
            ReviewPost reviewPost = reviewPostService.getAllReview(null).get(0);

            ReviewPost review = reviewPostService.getReview(reviewPost.getId());

            assertThat(review.getAuthor()).isEqualTo(user);
            assertThat(review.getContent()).isEqualTo("review");
            assertThat(review.getReviewPicUrl()).isEqualTo("url");
        }

        @DisplayName("리뷰가 존재하지 않으면 예외 발생")
        @Test
        void getReview2() {
            User user = getUser(1);
            ReviewPost reviewPost = reviewPostService.getAllReview(null).get(0);
            reviewPostService.withdrawReview(reviewPost.getId(), user);

            assertThatThrownBy(() -> reviewPostService.getReview(reviewPost.getId()))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.REVIEW_NOT_FOUND.getDescription());
        }
    }

    private Study getStudy(int index) {
        return studyService.getAllStudy(null).get(index);
    }

    private void createUser(String email, String nickname) {
        SignupRequestDto request = new SignupRequestDto(email, nickname, "a12345678!", null, null);
        userService.signup(request);
    }

    private User getLeader() {
        Long userId = userService.getAllId().get(0);
        return userService.getUserById(userId);
    }

    private User getUser(int index) {
        Long userId = userService.getAllId().get(index);
        return userService.getUserById(userId);
    }
}