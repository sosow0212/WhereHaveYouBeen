package hackathon.server.dto.history;

import hackathon.server.entity.history.History;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HistoryResponseDto {
    private Long historyId;
    private String guideName;
    private String productTitle;
    private String place;
    private int price;
    private Boolean isReviewed;

    public HistoryResponseDto toDto(History h) {
        return new HistoryResponseDto(h.getId(), h.getGuide().getName(), h.getProduct().getTitle(), h.place, h.getProduct().getPrice(), h.isReviewed());
    }
}
