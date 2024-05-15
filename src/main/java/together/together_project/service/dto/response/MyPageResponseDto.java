package together.together_project.service.dto.response;

import java.time.LocalDateTime;
import together.together_project.domain.User;

public record MyPageResponseDto(
        Long id,
        String email,
        String nickname,
        String bio,
        String profileUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {

    public static MyPageResponseDto from(User user) {
        return new MyPageResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getBio(),
                user.getProfileUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt()
        );
    }
}
