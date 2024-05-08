package together.together_project.service.dto.request;

import lombok.Getter;
import together.together_project.domain.User;

@Getter
public class SignupRequestDto {


    // @Email(message = "잘못된 이메일 형식입니다.")
    private String email;

    // @Length(min = 1, message = "닉네임은 1글자 이상으로 작성해주세요.")
    private String nickname;

    // @Pattern(regexp = PASSWORD_REGEX, message = "영문, 숫자, 특수문자 포함한 8자리 이상으로 작성해주세요.")
    private String password;

    private String bio;

    private String profileUrl;

    public User toUser(String encodedPassword) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .password(encodedPassword)
                .bio(bio)
                .profileUrl(profileUrl)
                .build();
    }
}
