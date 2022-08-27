package hackathon.server.dto.product;

import hackathon.server.dto.member.MemberSimpleResDto;
import hackathon.server.entity.product.Product;
import hackathon.server.entity.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponseDto {
    private Long id;
    private MemberSimpleResDto guide;
    private List<Tag> tags;
    private String title;
    private String content;
    private String place;
    private int price;
    private boolean isOnline;
    private LocalDateTime createdAt;

    public ProductResponseDto toDto(Product product) {
        return new ProductResponseDto(product.getId(), new MemberSimpleResDto().toDto(product.getGuide()), product.getGuide().getTags(), product.getTitle(), product.getContent(), product.getPlace(), product.getPrice(), product.isOnline(), product.getCreatedAt());
    }
}
