package together.together_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.service.StudyService;
import together.together_project.service.dto.request.StudiesRequestDto;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.StudiesResponseDto;

@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping()
    public ResponseEntity<ResponseBody> write(
            @Valid @RequestBody StudiesRequestDto request,
            @AuthUser User user
    ) {
        Study study = studyService.createStudyPost(request, user);
        StudiesResponseDto response = StudiesResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }
}
