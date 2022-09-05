package hackathon.server.dto.member;

import hackathon.server.entity.matching.Matching;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberTripsResponseDto {
    private Long matchId;
    private String guideNickname;
    private String productProductName;
    private boolean cancelledByUser;
    private boolean cancelledByGuide;
    private boolean finishedByUser;
    private boolean finishedByGuide;

    public MemberTripsResponseDto toDto(Matching m) {
        return new MemberTripsResponseDto(m.getId(), m.getGuide().getNickname(), m.getProduct().getTitle(), m.isCancelledByUser(), m.isCancelledByGuide(), m.isFinishedByUser(), m.isFinishedByGuide());
    }
}
