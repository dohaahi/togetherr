package together.together_project.validator;

import java.util.Optional;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

public class UserValidator {

    public static void verifySignup(Optional<User> userByEmail, Optional<User> userByNickname) {
        verifyEmailDuplicate(userByEmail);
        verifyNicknameDuplicate(userByNickname);
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

    private static void verifyEmailDuplicate(Optional<User> user) {
        if (user.isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATE);
        }
    }

    private static void verifyNicknameDuplicate(Optional<User> user) {
        if (user.isPresent()) {
            throw new CustomException(ErrorCode.NICKNAME_DUPLICATE);
        }
    }
}
