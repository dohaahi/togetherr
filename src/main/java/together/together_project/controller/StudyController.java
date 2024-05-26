package together.together_project.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.service.StudyService;
import together.together_project.service.dto.PaginationCollection;
import together.together_project.service.dto.PaginationRequestDto;
import together.together_project.service.dto.PaginationResponseDto;
import together.together_project.service.dto.request.StudiesRequestDto;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.StudiesResponseDto;
import together.together_project.service.dto.response.StudyPostsResponseDto;
import together.together_project.service.dto.response.StudyResponseDto;

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

    @GetMapping()
    public ResponseEntity<ResponseBody> getAllStudyPost(PaginationRequestDto request) {
        List<StudyPostsResponseDto> studies = studyService.getAllStudy(request)
                .stream()
                .map(StudyPostsResponseDto::from)
                .toList();

        PaginationCollection<StudyPostsResponseDto> data = PaginationCollection.from(studies);

        PaginationResponseDto<StudyPostsResponseDto> response = new PaginationResponseDto<>(
                data.getCurrentData(),
                data.totalElementsCount(),
                data.getNextCursor()
        );

        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/{study-post-id}")
    public ResponseEntity<ResponseBody> getById(@PathVariable("study-post-id") Long id) {
        Study study = studyService.getById(id);
        StudyResponseDto response = StudyResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

}
