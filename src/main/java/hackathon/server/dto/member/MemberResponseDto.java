package hackathon.server.dto.member;

import hackathon.server.entity.member.Authority;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberResponseDto {
    private String username;

    private String name;

    private String nickname;

    private List<Tag> tags;

    private String email;

    private String phone;

    private String address;

    private int money;

    private Authority authority;

    public MemberResponseDto toDto(Member m) {
        return new MemberResponseDto(m.getUsername(), m.getName(), m.getNickname(), m.getTags(), m.getEmail(), m.getPhone(), m.getAddress(), m.getMoney(), m.getAuthority());
    }
}
