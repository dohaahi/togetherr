package together.together_project.service.dto.response;

public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {
    public static LoginResponseDto from(String accessToken, String refreshToken) {

        return new LoginResponseDto(
                accessToken,
                refreshToken
        );
    }
}
