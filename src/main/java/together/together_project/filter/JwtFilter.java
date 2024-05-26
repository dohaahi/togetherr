package together.together_project.filter;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
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
import together.together_project.exception.ErrorCode;
import together.together_project.exception.ErrorResponse;
import together.together_project.security.JwtProvider;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTH_URI = "/api/auth";
    private static final String STUDIES_URI = "/api/studies";
    private static final String GET_METHOD = "GET";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 회원가입, 로그인의 경우 필터 실행 X
        if (request.getRequestURI().startsWith(AUTH_URI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 게시물 조회의 경우 필터 실행 X
        if (request.getRequestURI().startsWith(STUDIES_URI) && request.getMethod().equals(GET_METHOD)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // request에서 토큰 꺼내기
            String accessToken = resolveTokenFromRequest(response, request);
            jwtProvider.verifyAuthTokenOrThrow(accessToken);
        } catch (JwtException exception) {
            jwtExceptionHandler(response, exception.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        Cookie[] cookies = request.getCookies();
        if (isEmpty(cookies)) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("accessToken"))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new JwtException(ErrorCode.TOKEN_NOT_FOUND.getDescription()));
    }

    private void jwtExceptionHandler(HttpServletResponse response, String error) {
        int statusCode = 403;

        response.setStatus(statusCode);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        try {
            String json = new ObjectMapper()
                    .writeValueAsString(ErrorResponse.builder()
                            .data(null)
                            .error(error)
                            .statusCode(statusCode)
                            .build()
                    );
            response.getWriter().write(json);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
