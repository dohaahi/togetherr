package together.together_project.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    EMAIL_DUPLICATE("이미 존재하는 이메일입니다.", 409),
    EMAIL_FORMAT_INVALID("잘못된 이메일 양식입니다.", 400),
    NICKNAME_DUPLICATE("이미 존재하는 닉네임입니다.", 409),
    NICKNAME_LENGTH("닉네임은 1글자 이상으로 작성해주세요.", 400),
    PASSWORD_FORMAT_VALIDATE("영문, 숫자, 특수문자 포함한 8자리 이상으로 작성해주세요.", 400),
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다.", 401),
    AUTHENTICATION_FAILED("존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다.", 401),
    USER_NOT_FOUND("존재하지 않는 회원입니다.", 401),

    TOKEN_VALIDATE("유효하지 않은 토큰입니다.", 401),
    AUTHENTICATION_REQUIRED("로그인이 필요합니다.", 401),
    TOKEN_EXPIRED("유효기간이 만료된 토큰입니다.", 401),

    MAX_PEOPLE_UNDER_LIMIT("최대 인원은 2명 이상이여야 합니다.", 422),
    STUDY_NOT_FOUND("존재하지 않는 스터디입니다.", 400),
    STUDY_IS_FULLED("이미 모집이 완료된 스터디입니다.", 400),
    STUDY_ALREADY_JOINED("이미 참여 신청한 스터디입니다.", 400),
    STUDY_ALREADY_WITHDRAW("이미 철회 신청한 스터디입니다.", 404),
    POST_BUMP_PERIOD_EXCEPTION("끌어올리기는 이틀이 지나야 가능합니다.", 409),

    COMMENT_NOT_FOUND("존재하지 않는 댓글입니다.", 400),

    REVIEW_NOT_FOUND("존재하지 않는 리뷰입니다.", 4040),
    REVIEW_DUPLICATE("리뷰를 이미 작성하였습니다.", 400),

    LIKE_LINK_NOT_FOUND("존재하지 않는 좋아요입니다.", 404),

    EMPTY_CONTENT_ERROR("내용을 입력하지 않았습니다.", 400),
    DATA_NOT_FOUND("더 이상 가져올 데이터가 없습니다.", 400),
    UNAUTHORIZED_ACCESS("접근 권한이 없는 사용자입니다.", 403),
    INVALID_REQUEST("잘못된 요청건입니다.", 400),
    UNKNOWN_ERROR("알 수 없는 에러가 발생했습니다.", 500);


    private final String description;
    private final int statusCode;

    public static final String EMAIL_REGEX = "^[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}+$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
}
