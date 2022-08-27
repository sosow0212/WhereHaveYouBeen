package hackathon.server.entity.product;

import hackathon.server.entity.common.EntityDate;
import hackathon.server.entity.member.Member;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class Product extends EntityDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member guide;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String place;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private boolean isOnline;


    public Product(Member guide, String title, String content, String place, int price, boolean type) {
        this.guide = guide;
        this.title = title;
        this.content = content;
        this.place = place;
        this.price = price;
        this.isOnline = type;
    }
}
