package together.together_project.filter;

import static org.springframework.util.ObjectUtils.isEmpty;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.security.JwtProvider;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private static final String AUTH_URI = "/api/auth";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 회원가입, 로그인의 경우 필터 실행 X
        if (request.getRequestURI().startsWith(AUTH_URI + "/signup") ||
                request.getRequestURI().startsWith(AUTH_URI + "/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // request에서 토큰 꺼내기
        String accessToken = resolveTokenFromRequest(request);

        jwtProvider.verifyAuthTokenOrThrow(accessToken);

        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (isEmpty(cookies)) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("accessToken"))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_VALIDATE));
    }
}
