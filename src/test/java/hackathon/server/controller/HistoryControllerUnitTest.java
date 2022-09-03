package hackathon.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.server.controller.history.HistoryController;
import hackathon.server.dto.history.ReviewCreateRequestDto;
import hackathon.server.entity.member.Member;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.service.history.HistoryService;
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

import static hackathon.server.factory.MemberFactory.createUser;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class HistoryControllerUnitTest {
    @InjectMocks
    HistoryController historyController;

    @Mock
    HistoryService historyService;

    @Mock
    MemberRepository memberRepository;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(historyController).build();
    }

    @Test
    @DisplayName("리뷰 작성")
    public void createTest() throws Exception {
        // given
        Long id = 1L;
        Member member = createUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        ReviewCreateRequestDto req = new ReviewCreateRequestDto("리뷰 내용", 5);

        // when
        mockMvc.perform(
                post("/api/histories/{historyId}", id)
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

        // then
        verify(historyService).create(id, req, member);
    }

    @Test
    @DisplayName("전체 거래 내역 조회")
    public void findAllTest() throws Exception {
        // given
        Member member = createUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                get("/api/histories")
        ).andExpect(status().isOk());

        // then
        verify(historyService).findAll(member);
    }

    @Test
    @DisplayName("거래 내역 단건 조회")
    public void findTest() throws Exception {
        // given
        Long id = 1L;
        Member member = createUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                get("/api/histories/{historyId}", id)
        ).andExpect(status().isOk());

        // then
        verify(historyService).find(id, member);
    }
}
