package hackathon.server.factory;

import hackathon.server.entity.member.Authority;
import hackathon.server.entity.member.Member;

public class MemberFactory {

    public static Member createUser() {
        Member member = Member.builder()
                .username("user")
                .password("user123!")
                .email("user@test.com")
                .authority(Authority.ROLE_USER)
                .name("유저")
                .nickname("닉네임")
                .phone("010-1111-1111")
                .address("경기도~")
                .build();

        return member;
    }

    public static Member createGuide() {
        Member member = Member.builder()
                .username("guide")
                .password("user123!")
                .email("user2@test.com")
                .authority(Authority.ROLE_GUIDE)
                .name("가이드")
                .nickname("닉네임")
                .phone("010-1111-1111")
                .address("경기도~")
                .build();

        return member;
    }
}
