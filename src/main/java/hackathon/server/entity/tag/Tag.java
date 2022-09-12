package hackathon.server.entity.tag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hackathon.server.entity.common.EntityDate;
import hackathon.server.entity.member.Authority;
import hackathon.server.entity.member.Member;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tag extends EntityDate{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 태그명

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column(nullable = false)
    private boolean checkGuide;

    public Tag(String name, Member member) {
        this.name = name;
        this.member = member;
        this.checkGuide = checkIsGuide(member);
    }

    public boolean checkIsGuide(Member member) {
        if(member.getAuthority().equals(Authority.ROLE_GUIDE)) {
            return true;
        } else {
            return false;
        }
    }
}
