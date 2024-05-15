package together.together_project.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ErrorResponse handleException(
            CustomException exception) {

        return ErrorResponse.builder()
                .data(null)
                .error(exception.getErrorCode().getDescription())
                .statusCode(exception.getErrorCode().getStatusCode())
                .build();
    }
}

