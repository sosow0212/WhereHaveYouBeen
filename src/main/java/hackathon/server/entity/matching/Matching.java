package hackathon.server.entity.matching;

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

// Match 예약어라 바꿔야함
public class Matching extends EntityDate {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member guide;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @Column(nullable = false)
    private int tempMoney; // 임시 금액

    // 매칭 취소시 true
    @Column(nullable = false)
    private boolean cancelledByUser;

    @Column(nullable = false)
    private boolean cancelledByGuide;

    // 교환 완료시 true
    @Column(nullable = false)
    private boolean finishedByUser;

    @Column(nullable = false)
    private boolean finishedByGuide;

    @Column(nullable = false)
    private boolean finished;


    public Matching(Member user, Member guide, Product product) {
        this.user = user;
        this.guide = guide;
        this.product = product;
        this.tempMoney = product.getPrice();
        this.finishedByUser = this.finishedByGuide = this.cancelledByUser = this.cancelledByGuide = this.finished = false;
    }

    public void cancelMatchByUser() {
        this.cancelledByUser = true;
        if(cancelledByGuide && cancelledByUser) {
            this.finished = true;
        }
    }

    public void cancelMatchByGuide() {
        this.cancelledByGuide = true;
        if(cancelledByGuide && cancelledByUser) {
            this.finished = true;
        }
    }

    public boolean isCancelledMatch() {
        return isCancelledByUser() & isCancelledByGuide();
    }

    public void finishByUser() {
        this.finishedByUser = true;
        if(finishedByUser && finishedByGuide) {
            this.finished = true;
        }
    }

    public void finishByGuide() {
        this.finishedByGuide = true;
        if(finishedByUser && finishedByGuide) {
            this.finished = true;
        }
    }

    public boolean isFinishedMatch() {
        return isFinishedByUser() && isFinishedByGuide();
    }

}
