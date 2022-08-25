package hackathon.server.service;

import hackathon.server.dto.tag.TagEditRequestDto;
import hackathon.server.dto.tag.TagResponseDto;
import hackathon.server.dto.tag.TagUploadRequestDto;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.tag.Tag;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.repository.tag.TagRepository;
import hackathon.server.service.tag.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static hackathon.server.factory.MemberFactory.createUser;
import static hackathon.server.factory.TagFactory.createTag;
import static hackathon.server.factory.TagFactory.createTag2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TagServiceUnitTest {
    @InjectMocks
    TagService tagService;

    @Mock
    TagRepository tagRepository;

    @Mock
    MemberRepository memberRepository;

    private final int MIN_SIZE = 1;
    private final int MAX_SIZE = 4;

    @Test
    @DisplayName("본인 태그 조회")
    public void getOwnTagsTest() {
        // given
        Member member = createUser();
        List<Tag> tagList = new ArrayList<>();
        tagList.add(createTag(member));
        given(tagRepository.findAllByMember(member)).willReturn(tagList);

        // when
        List<TagResponseDto> result = tagService.getOwnTags(member);

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("타인 태그 조회")
    public void getSomeoneTags() {
        // given
        Long id = 1L;
        Member member = createUser();
        List<Tag> tagList = new ArrayList<>();
        tagList.add(createTag(member));
        given(memberRepository.findById(id)).willReturn(Optional.of(member));
        given(tagRepository.findAllByMember(member)).willReturn(tagList);

        // when
        List<TagResponseDto> result = tagService.getSomeoneTags(id);

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("태그 처음 저장")
    public void tagUploadTest() {
        // given
        List<String> tags = new ArrayList<>();
        tags.add("a");
        TagUploadRequestDto req = new TagUploadRequestDto(tags);
        Member member = createUser();
        given(tagRepository.existsByMember(any())).willReturn(false);

        // when
        tagService.tagUpload(req, any());

        // then
        verify(tagRepository).saveAll(any());
    }


    @Test
    @DisplayName("태그 수정")
    public void editTest() {
        // given
        Member member = createUser();
        List<Tag> addedTags = new ArrayList<>();
        addedTags.add(createTag(member));
        List<Tag> deletedTags = new ArrayList<>();
        deletedTags.add(createTag2(member));
        TagEditRequestDto req = new TagEditRequestDto(addedTags, deletedTags);

        List<Tag> tagList = new ArrayList<>(); // a, -b
        tagList.add(new Tag("test", member));
        tagList.add(new Tag("b", member));

        given(tagRepository.existsByMember(any())).willReturn(true);
        given(tagRepository.findAllByMember(any())).willReturn(tagList);
        given(tagRepository.existsTagByMemberAndNameContaining(member, "b")).willReturn(true);
        given(tagRepository.existsTagByMemberAndNameContaining(member, "a")).willReturn(false);
        given(tagRepository.findAllByMember(any())).willReturn(tagList);

        // when
        List<TagResponseDto> result = tagService.editTags(req, member);

        // then
        assertThat(result.size()).isEqualTo(2);
    }

}
