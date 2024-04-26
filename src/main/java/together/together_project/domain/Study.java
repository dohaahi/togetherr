package together.together_project.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Study extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    // TODO: user랑 study가 FK로 연결이 되지 않아도 되는지
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User leader;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "study_post_id")
    private StudyPost studyPost;

    private String location;

    private int participantCount;

    private int maxPeople;
}
