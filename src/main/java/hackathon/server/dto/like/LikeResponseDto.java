package hackathon.server.dto.like;

import hackathon.server.entity.likes.Likes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LikeResponseDto {
    private Long productId;
    private String productTitle;

    public static LikeResponseDto toDto(Likes likes) {
        return new LikeResponseDto(likes.getProduct().getId(), likes.getProduct().getTitle());
    }
}
