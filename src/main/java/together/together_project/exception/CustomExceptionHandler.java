package together.together_project.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({CustomException.class, RuntimeException.class})
    public ErrorResponse handleException(
            CustomException exception,
            HttpServletRequest request
    ) {
        return ErrorResponse.builder()
                .data(null)
                .error(exception.getErrorCode().getDescription())
                .statusCode(exception.getErrorCode().getStatusCode())
                .build();
    }
}

