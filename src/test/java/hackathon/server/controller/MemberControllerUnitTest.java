package hackathon.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.server.controller.member.MemberController;
import hackathon.server.dto.member.MemberEditRequestDto;
import hackathon.server.entity.member.Member;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static hackathon.server.factory.MemberFactory.createGuide;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MemberControllerUnitTest {

    @InjectMocks
    MemberController memberController;

    @Mock
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    @DisplayName("회원 조회")
    public void getMemberTest() throws Exception {
        // given
        Member member = createGuide();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                get("/api/members")
        ).andExpect(status().isOk());

        // then
        verify(memberService).getMember(member);
    }

    @Test
    @DisplayName("회원정보 수정")
    public void editMemberTest() throws Exception {
        // given
        MemberEditRequestDto req = new MemberEditRequestDto("이름 수정", "닉네임 수정", "010-1111-1111", "주소");
        Member member = createGuide();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                put("/api/members")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // then
        verify(memberService).editMember(req, member);
    }

    @Test
    @DisplayName("회원 삭제")
    public void deleteMemberTest() throws Exception {
        // given
        MemberEditRequestDto req = new MemberEditRequestDto("이름 수정", "닉네임 수정", "010-1111-1111", "주소");
        Member member = createGuide();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                delete("/api/members")
        ).andExpect(status().isOk());

        // then
        verify(memberService).deleteMember(member);
    }

    @Test
    @DisplayName("진행중인 거래 확인")
    public void getTrips() throws Exception {
        // given
        MemberEditRequestDto req = new MemberEditRequestDto("이름 수정", "닉네임 수정", "010-1111-1111", "주소");
        Member member = createGuide();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                get("/api/members/trips")
        ).andExpect(status().isOk());

        // then
        verify(memberService).getTrips(member);
    }
}
