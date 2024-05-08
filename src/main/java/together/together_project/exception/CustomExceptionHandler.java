package together.together_project.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex,
//            HttpHeaders headers,
//            HttpStatusCode status,
//            WebRequest request
//    ) {
//        ErrorResponse response = new ErrorResponse(null,
//                ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
//                );
//        return super.handleMethodArgumentNotValid(ex, headers, status, request);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Response> processValidationError(MethodArgumentNotValidException exception) {
//        BindingResult bindingResult = exception.getBindingResult();
//
//        ArrayList<String> errors = new ArrayList<>();
//        for (FieldError fieldError : bindingResult.getFieldErrors()) {
//            errors.add(fieldError.getDefaultMessage());
//        }
//
//        return ResponseEntity.of(Response.error(errors));
//    }

    @ExceptionHandler(CustomException.class)
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

