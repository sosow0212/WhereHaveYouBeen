package hackathon.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.server.controller.match.MatchController;
import hackathon.server.entity.member.Member;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.service.match.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static hackathon.server.factory.MemberFactory.createGuide;
import static hackathon.server.factory.MemberFactory.createUser;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MatchControllerUnitTest {
    @InjectMocks
    MatchController matchController;

    @Mock
    MatchService matchService;

    @Mock
    MemberRepository memberRepository;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(matchController).build();
    }

    @Test
    @DisplayName("유저 매칭 신청")
    public void createMatchTest() throws Exception {
        // given
        Long productId = 1L;
        Member member = createUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                post("/api/matches/{id}", productId)
        ).andExpect(status().isCreated());

        // then
        verify(matchService).createMatch(productId, member);
    }


    @Test
    @DisplayName("관광객이 거래 완료 요청")
    public void finishedByUserTest() throws Exception {
        // given
        Long matchId = 1L;
        Member member = createUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                post("/api/matches/users/{matchId}", matchId)
        ).andExpect(status().isOk());

        // then
        verify(matchService).finishedByUser(matchId, member);
    }


    @Test
    @DisplayName("가이드가 거래 완료 요청")
    public void finishedByGuideTest() throws Exception {
        // given
        Long matchId = 1L;
        Member member = createGuide();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                post("/api/matches/guides/{matchId}", matchId)
        ).andExpect(status().isOk());

        // then
        verify(matchService).finishedByGuide(matchId, member);
    }


    @Test
    @DisplayName("관광객이 매칭 취소 요청")
    public void cancelledMatchByUserTest() throws Exception {
        // given
        Long matchId = 1L;
        Member member = createUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                delete("/api/matches/users/{matchId}", matchId)
        ).andExpect(status().isOk());

        // then
        verify(matchService).cancelledMatchByUser(matchId, member);
    }


    @Test
    @DisplayName("가이드가 매칭 취소 요청")
    public void cancelledMatchByGuideTest() throws Exception {
        // given
        Long matchId = 1L;
        Member member = createGuide();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                delete("/api/matches/guides/{matchId}", matchId)
        ).andExpect(status().isOk());

        // then
        verify(matchService).cancelledMatchByGuide(matchId, member);
    }
}
