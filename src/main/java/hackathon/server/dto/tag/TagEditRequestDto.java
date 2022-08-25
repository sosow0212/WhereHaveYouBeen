package hackathon.server.dto.tag;

import hackathon.server.entity.tag.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "태그 수정")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TagEditRequestDto {
    @ApiModelProperty(value = "추가된 태그", notes = "추가된 태그를 입력해주세요.")
    private List<Tag> addedTags = new ArrayList<>();

    @ApiModelProperty(value = "제거된 태그", notes = "제거된 태그를 입력해주세요.")
    private List<Tag> deletedTags = new ArrayList<>();
}
