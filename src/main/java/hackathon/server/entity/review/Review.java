package hackathon.server.entity.review;

import hackathon.server.entity.common.EntityDate;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review extends EntityDate {
    // 작성자 // 해당 상품 // 평점 1~5 // 리뷰 내용 --> 추후에 상품 만들고 만드는게 좋을듯

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Member member;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private int score;

    public Review(String content, Product product, Member member, int score) {
        this.content = content;
        this.product = product;
        this.member = member;
        this.score = score;
    }
}
