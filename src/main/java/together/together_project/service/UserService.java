package together.together_project.service;


import static together.together_project.validator.UserValidator.verifySignup;

import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import together.together_project.domain.User;
import together.together_project.repository.UserRepositoryImpl;
import together.together_project.service.dto.request.SignupRequestDto;
import together.together_project.service.dto.response.SignupResponseDto;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryImpl userRepository;
    private final BcryptService bcryptService;

    public SignupResponseDto signup(SignupRequestDto request) {

        Optional<User> userByEmail = userRepository.findByEmail(request.getEmail());
        Optional<User> userByNickname = userRepository.findByNickname(request.getNickname());

        verifySignup(userByEmail, userByNickname, request.getEmail(), request.getNickname(), request.getPassword());

        String encodedPassword = bcryptService.encodeBcrypt(request.getPassword());
        User hashedUser = request.toUser(encodedPassword);
        userRepository.save(hashedUser);

        return SignupResponseDto.from(hashedUser);
    }
}
