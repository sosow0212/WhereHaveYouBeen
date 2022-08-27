package hackathon.server.dto.product;

import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiOperation(value = "상품 등록 요청")
public class ProductCreateRequestDto {
    @ApiModelProperty(value = "제목", notes = "제목을 입력해주세요.", required = true, example = "환상의 도시 용인으로")
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @ApiModelProperty(value = "내용", notes = "내용을 입력해주세요.", required = true, example = "용인으로 와보셨나요?? ~~")
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @ApiModelProperty(value = "지역", notes = "지역을 입력해주세요.", required = true, example = "경기도 용인시")
    @NotBlank(message = "지역을 입력해주세요.")
    private String place;

    @ApiModelProperty(value = "가격", notes = "가격을 입력해주세요.", required = true, example = "30000")
    @NotNull(message = "상품 가격을 입력해주세요.")
    private Integer price;

    @ApiModelProperty(value = "매칭 타입(온/오프라인)", notes = "체크 = 온라인, 체크x(기본 값) = 오프라인", required = true ,example = "체크 == 온라인")
    @NotNull(message = "온라인 혹은 오프라인 매칭인지 체크를 해주세요.")
    private Boolean isOnline;

    public Product toDto(ProductCreateRequestDto req, Member member) {
        return Product.builder()
                .title(req.title)
                .content(req.content)
                .place(req.place)
                .price(req.price)
                .isOnline(req.isOnline)
                .guide(member)
                .build();
    }

}
