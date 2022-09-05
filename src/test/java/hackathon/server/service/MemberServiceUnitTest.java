package hackathon.server.service;

import hackathon.server.dto.member.MemberEditRequestDto;
import hackathon.server.dto.member.MemberResponseDto;
import hackathon.server.dto.member.MemberTripsResponseDto;
import hackathon.server.entity.matching.Matching;
import hackathon.server.entity.member.Member;
import hackathon.server.exception.MemberNicknameAlreadyExistsException;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.repository.match.MatchRepository;
import hackathon.server.service.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static hackathon.server.factory.MatchFactory.createMatch;
import static hackathon.server.factory.MemberFactory.createGuide;
import static hackathon.server.factory.MemberFactory.createUser;
import static hackathon.server.factory.ProductFactory.createProduct;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberServiceUnitTest {
    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    MatchRepository matchRepository;

    @Test
    @DisplayName("유저 조회")
    public void getMemberTest() {
        // given
        Member member = createUser();

        // when
        MemberResponseDto result = memberService.getMember(member);

        // then
        assertThat(result.getName()).isEqualTo(member.getName());
    }

    @Test
    @DisplayName("유저 정보 수정")
    public void editMemberTest() {
        // given
        MemberEditRequestDto req = new MemberEditRequestDto("이름 수정", "닉네임 수정", "010-1111-1111", "주소");
        Member member = createUser();
        given(memberRepository.existsByNickname(req.getNickname())).willReturn(false);
        given(memberRepository.existsByPhone(req.getPhone())).willReturn(false);

        // when
        memberService.editMember(req, member);

        // then
        assertThat(member.getName()).isEqualTo(req.getName());
    }

    @Test
    @DisplayName("회원 삭제")
    public void deleteMemberTest() {
        // given
        Member member = createUser();
        List<Matching> matchings = new ArrayList<>();

        // when
        memberService.deleteMember(member);

        // then
        verify(memberRepository).delete(member);
    }

    @Test
    @DisplayName("진행중인 거래 확인")
    public void getTripsTest() {
        // given
        Member member = createUser();
        List<Matching> memberTrips = new ArrayList<>();
        memberTrips.add(createMatch(member, createGuide(), createProduct(createGuide())));
        memberTrips.add(createMatch(member, createGuide(), createProduct(createGuide())));

        given(matchRepository.findAllByUserAndFinishedFalse(member)).willReturn(memberTrips);

        // when
        List<MemberTripsResponseDto> result = memberService.getTrips(member);

        // then
        assertThat(result.size()).isEqualTo(memberTrips.size());
    }

}
