package together.together_project.service.dto.response;

public record ResponseBody(
        Object data,
        String error,
        int statusCode
) {
}
