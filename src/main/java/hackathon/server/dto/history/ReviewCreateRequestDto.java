package hackathon.server.dto.history;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiOperation(value = "리뷰 생성 요청")
public class ReviewCreateRequestDto {

    @ApiModelProperty(value = "리뷰 내용", notes = "리뷰 내용을 입력해주세요.", required = true, example = "관광지 추천을 아주 잘해주세요.")
    @NotBlank(message = "리뷰 내용을 입력해주세요.")
    private String content;

    @ApiModelProperty(value = "평점", notes = "평점을 입력해주세요. (1~5)", required = true, example = "3")
    @Min(value = 1, message = "최소 평점은 1점입니다.")
    @Max(value = 5, message = "최대 평점은 5점입니다.")
    private int score;

}
