package together.together_project.service.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequestDto {

    // @NotNull
    private final String email;

    // @NotNull
    private final String password;
}
