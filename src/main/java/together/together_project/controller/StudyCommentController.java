package together.together_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.StudyPostComment;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
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

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping("/{study-post-comment-id}")
    public ResponseEntity<ResponseBody> updateComment(
            @PathVariable("study-post-id") Long studyId,
            @PathVariable("study-post-comment-id") Long commentId,
            @Valid @RequestBody CommentUpdateRequestDto request,
            @AuthUser User currentUser
    ) {
        verifyUserIsCommentAuthor(commentId, currentUser);

        StudyPostComment studyComment = studyCommentService.updateComment(studyId, commentId, request, currentUser);
        CommentUpdateResponseDto response = CommentUpdateResponseDto.from(studyComment);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @DeleteMapping("/{study-post-comment-id}")
    public ResponseEntity<ResponseBody> withdrawComment(
            @PathVariable("study-post-id") Long studyId,
            @PathVariable("study-post-comment-id") Long commentId,
            @AuthUser User currentUser
    ) {
        verifyUserIsCommentAuthor(commentId, currentUser);

        studyCommentService.withdrawComment(commentId);
        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping("/{study-post-comment-id}")
    public ResponseEntity<ResponseBody> writeChildComment(
            @PathVariable("study-post-id") Long studyId,
            @PathVariable("study-post-comment-id") Long commentId,
            @Valid @RequestBody CommentWriteRequestDto request,
            @AuthUser User currentUser
    ) {
        StudyPostComment studyComment = studyCommentService.writeChildComment(studyId, commentId, request, currentUser);
        CommentWriteResponseDto response = CommentWriteResponseDto.from(studyComment);

        ResponseBody body = new ResponseBody(response, null, HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }

    @PutMapping("/{study-post-comment-id}/{child-comment-id}")
    public ResponseEntity<ResponseBody> updateChildComment(
            @PathVariable("study-post-id") Long studyId,
            @PathVariable("study-post-comment-id") Long parentCommentId,
            @PathVariable("child-comment-id") Long childCommentId,
            @Valid @RequestBody CommentUpdateRequestDto request,
            @AuthUser User currentUser
    ) {
        verifyUserIsCommentAuthor(childCommentId, currentUser);
        StudyPostComment studyComment = studyCommentService.updateChildComment(
                studyId,
                parentCommentId,
                childCommentId,
                request
        );

        CommentUpdateResponseDto response = CommentUpdateResponseDto.from(studyComment);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @DeleteMapping("/{study-post-comment-id}/{child-comment-id}")
    public ResponseEntity<ResponseBody> deleteChildComment(
            @PathVariable("study-post-id") Long studyId,
            @PathVariable("study-post-comment-id") Long parentCommentId,
            @PathVariable("child-comment-id") Long childCommentId,
            @AuthUser User currentUser
    ) {
        verifyUserIsCommentAuthor(childCommentId, currentUser);
        studyCommentService.deleteChildComment(studyId, parentCommentId, childCommentId);

        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    private void verifyUserIsCommentAuthor(Long commentId, User currentUser) {
        StudyPostComment comment = studyCommentService.getCommentById(commentId);

        if (!currentUser.getId().equals(comment.getAuthor().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}
