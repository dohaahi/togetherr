package together.together_project.repository;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.UserStudyLink;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

@Repository
@RequiredArgsConstructor
public class UserStudyLinkRepositoryImpl {

    private final UserStudyLinkJpaRepository userStudyLinkRepository;

    public void save(UserStudyLink userStudyLink) {
        userStudyLinkRepository.save(userStudyLink);
    }

    public Optional<UserStudyLink> findByStudyIdAndUserId(Long studyId, Long userId) {
        return userStudyLinkRepository.findByStudyIdAndUserId(studyId, userId);
    }

    public List<UserStudyLink> paginateJoinRequest(Long cursor, Long studyId) {
        if (userStudyLinkRepository.findAll().isEmpty()) {
            throw new CustomException(ErrorCode.PARTICIPANTS_NOT_FOUND);
        } else if (null == cursor) {
            cursor = userStudyLinkRepository.findFirstOderByIdDesc() + 1;
        }

        return userStudyLinkRepository.paginateJoinRequest(cursor, (long) (PAGINATION_COUNT + 1), studyId);
    }
}
