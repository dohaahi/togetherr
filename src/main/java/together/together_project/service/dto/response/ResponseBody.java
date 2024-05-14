package together.together_project.service.dto.response;

import org.springframework.validation.FieldError;

public record ResponseBody(Object data, String error, int statusCode) {

    public static ResponseBody of(FieldError fieldError) {

        return new ResponseBody(
                null,
                fieldError.getDefaultMessage(),
                fieldError.hashCode());
    }
}
