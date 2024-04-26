package together.together_project.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`user`")
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    // TODO: 양방향보다 단방향으로 설계하는 이유 조사

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String email;

    private String nickname;

    private String password;

    private String bio;

    private String profileUrl;
}
