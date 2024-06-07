package together.together_project.resolver;

import static together.together_project.constant.UserConstant.ACCESS_TOKEN;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import together.together_project.controller.AuthUser;
import together.together_project.domain.User;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.repository.UserRepositoryImpl;
import together.together_project.security.JwtProvider;

@Component
@RequiredArgsConstructor
public class AuthUserResolver implements HandlerMethodArgumentResolver {

    private final UserRepositoryImpl userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthUserAnnotation = parameter.hasParameterAnnotation(AuthUser.class);
        boolean isUserType = User.class.isAssignableFrom(parameter.getParameterType());

        return hasAuthUserAnnotation && isUserType;
    }

    @Override
    public User resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Cookie accessToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(ACCESS_TOKEN))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.AUTHENTICATION_REQUIRED));

        User user = userRepository.findById(jwtProvider.verifyAuthTokenOrThrow(accessToken.getValue()))
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_VALIDATE));

        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return user;
    }
}
