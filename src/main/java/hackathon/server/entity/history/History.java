package hackathon.server.entity.history;

import hackathon.server.entity.common.EntityDate;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class History extends EntityDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Member user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Member guide;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Product product;

    @Column(nullable = false)
    public String place;

    @Column(nullable = false)
    private boolean reviewed;

    public History(Member user, Member guide, Product product) {
        this.user = user;
        this.guide = guide;
        this.product = product;
        this.place = product.getPlace();
        this.reviewed = false;
    }

    public boolean isReviewed() {
        return reviewed;
    }
}
