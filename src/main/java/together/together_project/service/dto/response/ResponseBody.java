package together.together_project.service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.validation.FieldError;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ResponseBody(Object data, String error, int statusCode) {

    public static ResponseBody of(FieldError fieldError) {

        return new ResponseBody(
                null,
                fieldError.getDefaultMessage(),
                fieldError.hashCode());
    }
}
