package together.together_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.StudyPostComment;
import together.together_project.domain.User;
import together.together_project.service.StudyCommentService;
import together.together_project.service.dto.request.WriteCommentRequestDto;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.WriteCommentResponseDto;

@RestController
@RequestMapping("studies")
@RequiredArgsConstructor
public class StudyCommentController {

    private final StudyCommentService studyCommentService;

    @PostMapping("/{study-post-id}/comments")
    public ResponseEntity<ResponseBody> writeComment(
            @PathVariable("study-post-id") Long studyId,
            @Valid @RequestBody WriteCommentRequestDto request,
            @AuthUser User currentUser
    ) {
        StudyPostComment studyComment = studyCommentService.write(request, studyId, currentUser);
        WriteCommentResponseDto response = WriteCommentResponseDto.from(studyComment);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }
}
