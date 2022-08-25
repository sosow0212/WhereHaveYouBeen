package hackathon.server.dto.tag;

import hackathon.server.entity.tag.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value = "태그 등록")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TagUploadRequestDto {

    @ApiModelProperty(value = "태그", notes = "태그를 입력해주세요", required = true, example = "A태그, B태그")
    @NotNull(message = "적어도 한 개 이상의 태그를 입력해주세요.")
    private List<String> tags;
}
