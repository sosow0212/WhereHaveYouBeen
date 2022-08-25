package hackathon.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.server.controller.tag.TagController;
import hackathon.server.dto.tag.TagEditRequestDto;
import hackathon.server.dto.tag.TagUploadRequestDto;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.tag.Tag;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.service.tag.TagService;
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

import static hackathon.server.factory.TagFactory.createTag;
import static hackathon.server.factory.TagFactory.createTag2;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static hackathon.server.factory.MemberFactory.createUser;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TagControllerUnitTest {
    @InjectMocks
    TagController tagController;

    @Mock
    TagService tagService;

    @Mock
    MemberRepository memberRepository;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
    }

    @Test
    @DisplayName("본인 태그 조회")
    public void getOwnTagsTest() throws Exception {
        // given
        Member member = createUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                        get("/api/tags"))
                .andExpect(status().isOk());

        // then
        verify(tagService).getOwnTags(member);
    }

    @Test
    @DisplayName("타인의 태그 조회")
    public void getSomeoneTagsTest() throws Exception {
        // given
        Long id = 1L;

        // when
        mockMvc.perform(
                        get("/api/tags/{id}", id))
                .andExpect(status().isOk());

        // then
        verify(tagService).getSomeoneTags(id);
    }

    @Test
    @DisplayName("관광객 태그 첫 등록")
    public void createTagsTest() throws Exception {
        // given
        Member member = createUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        List<String> tags = new ArrayList<>();
        tags.add("a");
        tags.add("b");
        TagUploadRequestDto req = new TagUploadRequestDto(tags);

        // when
        mockMvc.perform(
                post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isCreated());

        // then
        verify(tagService).tagUpload(req, member);
    }

    @Test
    @DisplayName("태그 수정하기")
    public void editTagsTest() throws Exception {
        // given
        Member member = createUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        List<Tag> addedTags = new ArrayList<>();
        addedTags.add(createTag(member));
        List<Tag> deletedTags = new ArrayList<>();
        deletedTags.add(createTag2(member));
        TagEditRequestDto req = new TagEditRequestDto(addedTags, deletedTags);

        // when, then
        mockMvc.perform(
                put("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isOk());
    }
}
