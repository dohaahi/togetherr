package together.together_project.repository;

import static together.together_project.domain.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import together.together_project.domain.User;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl {

    private final JPAQueryFactory q;
    private final UserJpaRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return q.select(user)
                .from(user)
                .where(user.email.eq(email))
                .stream()
                .findFirst();
    }

    public Optional<User> findByNickname(String nickname) {
        return q.select(user)
                .from(user)
                .where(user.nickname.eq(nickname))
                .stream()
                .findFirst();
    }
}
