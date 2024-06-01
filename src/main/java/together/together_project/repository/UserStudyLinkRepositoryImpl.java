package together.together_project.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.UserStudyLink;

@Repository
@RequiredArgsConstructor
public class UserStudyLinkRepositoryImpl {

    private final UserStudyLinkJpaRepository userStudyLinkRepository;

    public void save(UserStudyLink userStudyLink) {
        userStudyLinkRepository.save(userStudyLink);
    }

    public Optional<UserStudyLink> findByStudyId(Long studyId) {
        return userStudyLinkRepository.findById(studyId);
    }
}
