package hackathon.server.dto.product;

import hackathon.server.dto.member.MemberSimpleResponseDto;
import hackathon.server.entity.product.Product;
import hackathon.server.entity.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponseDto {
    private Long id;
    private MemberSimpleResponseDto guide;
    private List<Tag> tags;
    private String title;
    private String content;
    private String place;
    private int price;
    private boolean isOnline;
    private List<ImageDto> images;
    private LocalDateTime createdAt;

    public ProductResponseDto toDto(Product product) {
        return new ProductResponseDto(product.getId(), new MemberSimpleResponseDto().toDto(product.getGuide()), product.getGuide().getTags(), product.getTitle(), product.getContent(), product.getPlace(), product.getPrice(), product.isOnline(), product.getImages().stream().map(i -> ImageDto.toDto(i)).collect(Collectors.toList()) ,product.getCreatedAt());
    }
}
