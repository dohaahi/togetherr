package together.together_project.team.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import together.together_project.team.domain.Member;

public interface MemberJapRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);
}
