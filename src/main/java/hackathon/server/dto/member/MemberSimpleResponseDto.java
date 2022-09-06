package hackathon.server.dto.member;

import hackathon.server.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberSimpleResponseDto {
    private Long id;
    private String nickname;
    private String phone;
    private String email;
    // 추후에 평점 달기

    public MemberSimpleResponseDto toDto(Member member) {
        return new MemberSimpleResponseDto(member.getId(), member.getNickname(), member.getPhone(), member.getEmail());
    }
}
