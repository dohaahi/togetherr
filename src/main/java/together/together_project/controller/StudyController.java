package together.together_project.controller;


import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.Study;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.StudyService;
import together.together_project.service.dto.PaginationCollection;
import together.together_project.service.dto.PaginationRequestDto;
import together.together_project.service.dto.PaginationResponseDto;
import together.together_project.service.dto.request.StudyPostBumpRequestDto;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;
import together.together_project.service.dto.request.StudyPostUpdateRequestDto;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.StudyPostBumpResponseDto;
import together.together_project.service.dto.response.StudyPostCreateResponseDto;
import together.together_project.service.dto.response.StudyPostResponseDto;
import together.together_project.service.dto.response.StudyPostsResponseDto;

@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping()
    public ResponseEntity<ResponseBody> write(
            @Valid @RequestBody StudyPostCreateRequestDto request,
            @AuthUser User user
    ) {
        Study study = studyService.createStudyPost(request, user);
        StudyPostCreateResponseDto response = StudyPostCreateResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }

    @GetMapping()
    public ResponseEntity<ResponseBody> getAllStudyPost(@RequestBody PaginationRequestDto request) {
        List<StudyPostsResponseDto> studies = studyService.getAllStudy(request)
                .stream()
                .map(StudyPostsResponseDto::from)
                .toList();

        boolean hasMore = studies.size() == request.getCount() + 1;

        Long lastId = -1L;
        if (hasMore) {
            studies = studies.subList(0, studies.size() - 1);
            lastId = studies.get(studies.size() - 1).id();
        }

        PaginationCollection<StudyPostsResponseDto> data = PaginationCollection.of(hasMore, lastId, studies);

        PaginationResponseDto<StudyPostsResponseDto> response = new PaginationResponseDto<>(
                data.hasMore(),
                data.getNextCursor(),
                data.getCurrentData()
        );

        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/{study-post-id}")
    public ResponseEntity<ResponseBody> getById(@PathVariable("study-post-id") Long id) {
        Study study = studyService.getById(id);
        StudyPostResponseDto response = StudyPostResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PutMapping("/{study-post-id}")
    public ResponseEntity<ResponseBody> updateStudyPost(
            @PathVariable("study-post-id") Long id,
            @RequestBody StudyPostUpdateRequestDto request,
            @AuthUser User currentUser
    ) {
        if (!currentUser.getId().equals(studyService.getById(id).getLeader().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_POST_EDIT);
        }

        Study study = studyService.updateStudyPost(id, request);
        StudyPostUpdateResponseDto response = StudyPostUpdateResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PutMapping("/{study-post-id}/bump")
    public ResponseEntity<ResponseBody> bumpStudyPost(
            @PathVariable("study-post-id") Long id,
            @Valid @RequestBody StudyPostBumpRequestDto request,
            @AuthUser User currnetUser
    ) {
        if (!currnetUser.getId().equals(studyService.getById(id).getLeader().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_POST_EDIT);
        }

        Study study = studyService.bumpStudyPost(id, request);
        StudyPostBumpResponseDto response = StudyPostBumpResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }
}
