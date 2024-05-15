package together.together_project.service.dto.request;

import jakarta.validation.constraints.NotNull;

public record WithdrawRequestDto(
        @NotNull
        String password
) {
}
