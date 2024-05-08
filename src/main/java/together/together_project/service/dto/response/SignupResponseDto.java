package together.together_project.service.dto.response;

import java.time.LocalDateTime;
import together.together_project.domain.User;

public record SignupResponseDto(
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt

) {

    static public SignupResponseDto from(User user) {
        return new SignupResponseDto(
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt()
        );
    }
}
