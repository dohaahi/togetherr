package together.together_project.controller;


import static together.together_project.constant.StudyConstant.PAGINATION_COUNT;

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
import together.together_project.domain.User;
import together.together_project.domain.UserStudyJoinStatus;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
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
import together.together_project.service.dto.response.StudyPostBumpResponseDto;
import together.together_project.service.dto.response.StudyPostCreateResponseDto;
import together.together_project.service.dto.response.StudyPostResponseDto;
import together.together_project.service.dto.response.StudyPostUpdateResponseDto;
import together.together_project.service.dto.response.StudyPostsResponseDto;

@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final UserStudyLinkService userStudyLinkService;

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
    public ResponseEntity<ResponseBody> getAllStudyPost(
            @RequestParam(value = "cursor", required = false) Long cursor
    ) {
        List<StudyPostsResponseDto> studies = studyService.getAllStudy((Long) cursor)
                .stream()
                .map(StudyPostsResponseDto::from)
                .toList();

        boolean hasMore = studies.size() == PAGINATION_COUNT + 1;

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

    @GetMapping("/{study-id}")
    public ResponseEntity<ResponseBody> getById(@PathVariable("study-id") Long id) {
        Study study = studyService.getById(id);
        StudyPostResponseDto response = StudyPostResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PutMapping("/{study-id}")
    public ResponseEntity<ResponseBody> updateStudyPost(
            @PathVariable("study-id") Long id,
            @RequestBody StudyPostUpdateRequestDto request,
            @AuthUser User currentUser
    ) {
        verifyUserIsStudyLeader(currentUser, id);

        Study study = studyService.updateStudyPost(id, request);
        StudyPostUpdateResponseDto response = StudyPostUpdateResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PutMapping("/{study-id}/bump")
    public ResponseEntity<ResponseBody> bumpStudyPost(
            @PathVariable("study-id") Long id,
            @Valid @RequestBody StudyPostBumpRequestDto request,
            @AuthUser User currentUser
    ) {
        verifyUserIsStudyLeader(currentUser, id);

        Study study = studyService.bumpStudyPost(id, request);
        StudyPostBumpResponseDto response = StudyPostBumpResponseDto.from(study);
        ResponseBody body = new ResponseBody(response, null, HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @DeleteMapping("/{study-id}")
    public ResponseEntity<ResponseBody> deletePost(
            @PathVariable("study-id") Long id,
            @AuthUser User currentUser
    ) {
        verifyUserIsStudyLeader(currentUser, id);

        studyService.deleteStudy(id);
        userStudyLinkService.deleteByStudyId(id);
        ResponseBody body = new ResponseBody(null, null, HttpStatus.OK.value());

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

    @GetMapping("/{study-id}/requests")
    public ResponseEntity<ResponseBody> getAllJoinRequest(
            @PathVariable("study-id") Long studyId,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @AuthUser User currentUser
    ) {
        verifyUserIsStudyLeader(currentUser, studyId);

        List<JoinRequestsResponseDto> joinRequests = userStudyLinkService.getAllJoinRequest(cursor, studyId)
                .stream()
                .map(JoinRequestsResponseDto::from)
                .toList();

        boolean hasMore = joinRequests.size() == PAGINATION_COUNT + 1;

        Long lastId = -1L;
        if (hasMore) {
            joinRequests.subList(0, joinRequests.size() - 1);
            lastId = joinRequests.get(joinRequests.size() - 1).id();
        }

        PaginationCollection<JoinRequestsResponseDto> data = PaginationCollection.of(hasMore, lastId, joinRequests);

        PaginationResponseDto<JoinRequestsResponseDto> response = new PaginationResponseDto<>(
                data.hasMore(),
                data.getNextCursor(),
                data.getCurrentData()
        );

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
