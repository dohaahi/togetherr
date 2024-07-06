package together.together_project.controller;


import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import together.together_project.domain.Study;
import together.together_project.domain.StudyPostLikeLink;
import together.together_project.domain.User;
import together.together_project.domain.UserStudyJoinStatus;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.StudyPostLikeService;
import together.together_project.service.StudyService;
import together.together_project.service.UserStudyLinkService;
import together.together_project.service.dto.PaginationCollection;
import together.together_project.service.dto.PaginationResponseDto;
import together.together_project.service.dto.request.RespondToJoinRequestDto;
import together.together_project.service.dto.request.StudyPostBumpRequestDto;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;
import together.together_project.service.dto.request.StudyPostUpdateRequestDto;
import together.together_project.service.dto.response.JoinRequestsResponseDto;
import together.together_project.service.dto.response.RespondToJoinResponseDto;
import together.together_project.service.dto.response.ResponseBody;
import together.together_project.service.dto.response.StudyJoinResponseDto;
import together.together_project.service.dto.response.StudyParticipantsResponseDto;
import together.together_project.service.dto.response.StudyPostBumpResponseDto;
import together.together_project.service.dto.response.StudyPostCreateResponseDto;
import together.together_project.service.dto.response.StudyPostLikeResponseDto;
import together.together_project.service.dto.response.StudyPostLikesResponseDto;
import together.together_project.service.dto.response.StudyPostResponseDto;
import together.together_project.service.dto.response.StudyPostUpdateResponseDto;
import together.together_project.service.dto.response.StudyPostsResponseDto;

@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final UserStudyLinkService userStudyLinkService;
    private final StudyPostLikeService studyPostLikeService;

    @PostMapping()
    public ResponseEntity<ResponseBody> write(
            @Valid @RequestBody StudyPostCreateRequestDto request,
            @AuthUser User currentUser
    ) {
        Study study = studyService.createStudyPost(request, currentUser);
        StudyPostCreateResponseDto response = StudyPostCreateResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }

    @GetMapping()
    public ResponseEntity<ResponseBody> getAllStudyPost(
            @RequestParam(value = "cursor", required = false) Long cursor
    ) {
        List<StudyPostsResponseDto> studies = studyService.getAllStudy(cursor)
                .stream()
                .map(StudyPostsResponseDto::from)
                .toList();

        PaginationCollection<StudyPostsResponseDto> collection = PaginationCollection.of(
                studies, StudyPostsResponseDto::id);
        PaginationResponseDto<StudyPostsResponseDto> response = PaginationResponseDto.of(
                collection);

        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/{study-id}")
    public ResponseEntity<ResponseBody> getById(@PathVariable("study-id") Long studyId) {
        Study study = studyService.getById(studyId);
        StudyPostResponseDto response = StudyPostResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PutMapping("/{study-id}")
    public ResponseEntity<ResponseBody> updateStudyPost(
            @PathVariable("study-id") Long studyId,
            @RequestBody StudyPostUpdateRequestDto request,
            @AuthUser User currentUser
    ) {
        verifyUserIsStudyLeader(currentUser, studyId);

        Study study = studyService.updateStudyPost(studyId, request);
        StudyPostUpdateResponseDto response = StudyPostUpdateResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PutMapping("/{study-id}/bump")
    public ResponseEntity<ResponseBody> bumpStudyPost(
            @PathVariable("study-id") Long studyId,
            @Valid @RequestBody StudyPostBumpRequestDto request,
            @AuthUser User currentUser
    ) {
        verifyUserIsStudyLeader(currentUser, studyId);

        Study study = studyService.bumpStudyPost(studyId, request);
        StudyPostBumpResponseDto response = StudyPostBumpResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @DeleteMapping("/{study-id}")
    public ResponseEntity<ResponseBody> deletePost(
            @PathVariable("study-id") Long studyId,
            @AuthUser User currentUser
    ) {
        verifyUserIsStudyLeader(currentUser, studyId);

        studyService.deleteStudy(studyId);
        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/{study-id}/requests")
    public ResponseEntity<ResponseBody> getAllJoinRequest(
            @PathVariable("study-id") Long studyId,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @AuthUser User currentUser
    ) {
        verifyUserIsStudyLeader(currentUser, studyId);

        List<JoinRequestsResponseDto> joinRequests = userStudyLinkService.getAllJoinRequest(studyId, cursor)
                .stream()
                .map(JoinRequestsResponseDto::from)
                .toList();

        PaginationCollection<JoinRequestsResponseDto> collection = PaginationCollection.of(joinRequests,
                JoinRequestsResponseDto::id);
        PaginationResponseDto<JoinRequestsResponseDto> response = PaginationResponseDto.of(collection);

        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping("/{study-id}/members")
    public ResponseEntity<ResponseBody> getAllParticipants(
            @PathVariable("study-id") Long studyId,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @AuthUser User currentUser
    ) {
        List<StudyParticipantsResponseDto> participants = userStudyLinkService.getAllParticipants(studyId, cursor)
                .stream()
                .map(StudyParticipantsResponseDto::from)
                .toList();

        PaginationCollection<StudyParticipantsResponseDto> collection = PaginationCollection.of(
                participants, StudyParticipantsResponseDto::id);
        PaginationResponseDto<StudyParticipantsResponseDto> response = PaginationResponseDto.of(
                collection);

        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PostMapping("/{study-id}/request")
    public ResponseEntity<ResponseBody> requestToJoinStudy(
            @PathVariable("study-id") Long studyId,
            @AuthUser User currentUser
    ) {
        userStudyLinkService.join(studyId, currentUser);
        StudyJoinResponseDto response = StudyJoinResponseDto.from(UserStudyJoinStatus.PENDING);
        ResponseBody body = new ResponseBody(
                response,
                null,
                HttpStatus.NO_CONTENT.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @DeleteMapping("/{study-id}/request")
    public ResponseEntity<ResponseBody> withdrawJoinStudyRequest(
            @PathVariable("study-id") Long studyId,
            @AuthUser User currentUser
    ) {
        studyService.getById(studyId);
        userStudyLinkService.withdrawJoinStudyRequest(studyId, currentUser.getId());

        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @DeleteMapping("/{study-id}/members")
    public ResponseEntity<ResponseBody> withdrawParticipation(
            @PathVariable("study-id") Long studyId,
            @AuthUser User currentUser
    ) {
        Study study = studyService.getById(studyId);
        if (study.getLeader().getId() == currentUser.getId()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        userStudyLinkService.withdrawParticipation(studyId, currentUser.getId());

        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PostMapping("/{study-id}/response-request")
    public ResponseEntity<ResponseBody> respondToJoinRequest(
            @PathVariable("study-id") Long studyId,
            @Valid @RequestBody RespondToJoinRequestDto request,
            @AuthUser User currentUser
    ) {
        verifyUserIsStudyLeader(currentUser, studyId);

        UserStudyJoinStatus respondToJoinRequest = userStudyLinkService.respondToJoinRequest(request, studyId);
        RespondToJoinResponseDto response = RespondToJoinResponseDto.from(respondToJoinRequest);
        ResponseBody body = new ResponseBody(
                response,
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PostMapping("/{study-id}/likes")
    public ResponseEntity<ResponseBody> likeStudy(
            @PathVariable("study-id") Long studyId,
            @AuthUser User currentUser
    ) {
        StudyPostLikeLink studyPostLikeLink = studyPostLikeService.like(studyId, currentUser);
        StudyPostLikeResponseDto response = StudyPostLikeResponseDto.of(studyPostLikeLink);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }

    @GetMapping("/{study-id}/likes")
    public ResponseEntity<ResponseBody> getLikeStudy(
            @PathVariable("study-id") Long studyId,
            @RequestParam(value = "cursor", required = false) Long cursor
    ) {
        List<StudyPostLikesResponseDto> likeLinks = studyPostLikeService.getStudyLike(studyId, cursor)
                .stream()
                .map(StudyPostLikesResponseDto::of)
                .toList();

        PaginationCollection<StudyPostLikesResponseDto> collection = (PaginationCollection<StudyPostLikesResponseDto>) PaginationCollection
                .of(likeLinks, StudyPostLikesResponseDto::id);
        PaginationResponseDto<StudyPostLikesResponseDto> response = PaginationResponseDto.of(
                collection);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    private void verifyUserIsStudyLeader(User currentUser, Long studyId) {
        if (!currentUser.getId().equals(studyService.getById(studyId).getLeader().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}
