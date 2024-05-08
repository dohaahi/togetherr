package together.together_project.exception;

import lombok.Builder;

@Builder
public record ErrorResponse(
        Object data,
        String error,
        int statusCode
) {
}
