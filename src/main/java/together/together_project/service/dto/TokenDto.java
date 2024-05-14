package together.together_project.service.dto;

public record TokenDto(
        String accessToken
) {
    public static TokenDto from(String accessToken) {
        return new TokenDto(
                accessToken
        );
    }
}
