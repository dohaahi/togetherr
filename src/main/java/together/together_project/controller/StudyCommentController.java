package together.together_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.StudyPostComment;
import together.together_project.domain.User;
import together.together_project.service.StudyCommentService;
import together.together_project.service.dto.request.CommentUpdateRequestDto;
import together.together_project.service.dto.request.CommentWriteRequestDto;
import together.together_project.service.dto.response.CommentUpdateResponseDto;
import together.together_project.service.dto.response.CommentWriteResponseDto;
import together.together_project.service.dto.response.ResponseBody;

@RestController
@RequestMapping("studies/{study-post-id}/comments")
@RequiredArgsConstructor
public class StudyCommentController {

    private final StudyCommentService studyCommentService;

    @PostMapping()
    public ResponseEntity<ResponseBody> writeComment(
            @PathVariable("study-post-id") Long studyId,
            @Valid @RequestBody CommentWriteRequestDto request,
            @AuthUser User currentUser
    ) {
        StudyPostComment studyComment = studyCommentService.write(request, studyId, currentUser);
        CommentWriteResponseDto response = CommentWriteResponseDto.from(studyComment);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }

    @PutMapping("/{study-post-comment-id}")
    public ResponseEntity<ResponseBody> updateComment(
            @PathVariable("study-post-id") Long studyId,
            @PathVariable("study-post-comment-id") Long commentId,
            @Valid @RequestBody CommentUpdateRequestDto request,
            @AuthUser User currentUser
    ) {
        StudyPostComment studyComment = studyCommentService.updateComment(studyId, commentId, request, currentUser);
        CommentUpdateResponseDto resonse = CommentUpdateResponseDto.from(studyComment);
        ResponseBody body = new ResponseBody(resonse, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

}
