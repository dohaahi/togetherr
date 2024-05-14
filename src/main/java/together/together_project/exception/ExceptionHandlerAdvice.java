package together.together_project.exception;

import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import together.together_project.service.dto.response.ResponseBody;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        ResponseBody body = ResponseBody.of(Objects.requireNonNull(e.getFieldError()));

        return ResponseEntity.badRequest()
                .body(body);
    }
}
