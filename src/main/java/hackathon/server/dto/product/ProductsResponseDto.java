package hackathon.server.dto.product;

import hackathon.server.entity.member.Member;
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
public class ProductsResponseDto {
    private Long id;
    private String guide_nickname;
    private List<Tag> tags;
    private String title;
    private String place;
    private int price;
    private LocalDateTime createdAt;

    public ProductsResponseDto toDto(Product product) {
        return new ProductsResponseDto(product.getId(), product.getGuide().getNickname(), product.getGuide().getTags(), product.getTitle(), product.getPlace(), product.getPrice(), product.getCreatedAt());
    }

}
