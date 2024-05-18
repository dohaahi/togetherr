package together.together_project.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.service.dto.request.StudiesRequestDto;

@SpringBootTest
@Transactional
class StudyServiceTest {

    @Autowired
    private StudyService studyService;

    @Test
    public void testCreateStudyPost() {
        StudiesRequestDto request = new StudiesRequestDto(
                "test title",
                "test content",
                "test location",
                5
        );

        User user = User.builder()
                .id(1L)
                .email("test@google.com")
                .nickname("test")
                .build();

        Study createdStudy = studyService.createStudyPost(request, user);

        Assertions.assertThat(createdStudy.getStudyPost().getTitle())
                .isEqualTo("test title");
    }

}