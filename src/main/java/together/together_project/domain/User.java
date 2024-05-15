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
import together.together_project.service.dto.request.MyPageRequestDto;

@Entity
@Table(name = "`user`")
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String email;

    private String nickname;

    private String password;

    private String bio;

    private String profileUrl;

    public User update(MyPageRequestDto request) {

        if (request.email() != null) {
            this.email = request.email();
        }

        if (request.nickname() != null) {
            this.nickname = request.nickname();
        }

        if (request.bio() != null) {
            this.bio = request.bio();
        }

        if (request.profileUrl() != null) {
            this.profileUrl = request.profileUrl();
        }

        // 수정 시간 변경
        this.updateTime();

        return this;
    }
}
