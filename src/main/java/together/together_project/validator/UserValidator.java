package together.together_project.validator;

import static together.together_project.exception.ErrorCode.EMAIL_REGEX;
import static together.together_project.exception.ErrorCode.PASSWORD_REGEX;

import java.util.Optional;
import java.util.regex.Pattern;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

public class UserValidator {

    // TODO: @Valid 사용하기

    public static void verifySignup(Optional<User> userByEmail, Optional<User> userByNickname, String email,
                                    String nickname, String password) {
        verifyEmailRegex(email);
        verifyEmailDuplicate(userByEmail);
        verifyNicknameLength(nickname);
        verifyNicknameDuplicate(userByNickname);
        verifyPasswordRegex(password);
    }

    public static void verifyLogin(Optional<User> user) {
        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }
    }

    public static void verifyWithdraw(Optional<User> user) {
        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.TOKEN_VALIDATE);
        }
    }

    private static void verifyEmailRegex(String email) {
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new CustomException(ErrorCode.EMAIL_FORMAT_INVALID);
        }
    }

    private static void verifyEmailDuplicate(Optional<User> user) {
        if (user.isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATE);
        }
    }

    private static void verifyNicknameLength(String nickname) {
        if (nickname.isEmpty()) {
            throw new CustomException(ErrorCode.NICKNAME_LENGTH);
        }
    }

    private static void verifyNicknameDuplicate(Optional<User> user) {
        if (user.isPresent()) {
            throw new CustomException(ErrorCode.NICKNAME_DUPLICATE);
        }
    }

    private static void verifyPasswordRegex(String password) {
        if (!Pattern.matches(PASSWORD_REGEX, password)) {
            throw new CustomException(ErrorCode.PASSWORD_FORMAT_VALIDATE);
        }
    }
}
