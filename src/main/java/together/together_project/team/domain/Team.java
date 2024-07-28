package together.together_project.team.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int memberCountLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member owner;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TeamStatus status = TeamStatus.PREPARING;

    @OneToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private List<Member> members = new ArrayList<>();

    public void modifyStatus(Member member, TeamStatus teamStatus) {
        throwIfNotOwner(member);
        this.status = teamStatus;
    }

    public void throwIfNotOwner(Member member) {
        if (!owner.getId().equals(member.getId())) {
            throw new IllegalArgumentException("권한 없음");
        }
    }

    public boolean canJoin(Member member) {
        boolean notContainsMember = members.stream().noneMatch(m -> m.getId() == member.getId());
        boolean isMyTeam = owner.getId().equals(member.getId());
        boolean isInviting = status == TeamStatus.INVITING;
        boolean isMemberCountNotOver = members.size() < memberCountLimit - 1;

        return !(notContainsMember && isMyTeam) && isInviting && isMemberCountNotOver;
    }

    public void addMember(Member member) {
        if (members.contains(member)) {
            throw new IllegalArgumentException("이미 가입");
        }

        if (member.hasTeam()) {
            throw new IllegalArgumentException("다튼 팀에 가입됨");
        }

        members.add(member);
        member.joinTeam(this);
    }

    public void setId(Long id) {
        this.id = id;
    }
}
