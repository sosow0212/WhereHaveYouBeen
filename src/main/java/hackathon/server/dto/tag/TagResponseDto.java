package hackathon.server.dto.tag;

import hackathon.server.entity.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TagResponseDto {
    private String tag;

    public TagResponseDto toDto(Tag tag) {
        return new TagResponseDto(tag.getName());
    }
}
