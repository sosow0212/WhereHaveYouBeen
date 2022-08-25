package hackathon.server.service.tag;

import hackathon.server.dto.tag.TagEditRequestDto;
import hackathon.server.dto.tag.TagResponseDto;
import hackathon.server.dto.tag.TagUploadRequestDto;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.tag.Tag;
import hackathon.server.exception.*;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.repository.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;

    private final int MIN_SIZE = 1;
    private final int MAX_SIZE = 4;

    @Transactional(readOnly = true)
    public List<TagResponseDto> getOwnTags(Member member) {
        List<Tag> tagList = tagRepository.findAllByMember(member);
        List<TagResponseDto> dtoList = new ArrayList<>();
        tagList.stream().forEach(i -> dtoList.add(new TagResponseDto().toDto(i)));

        return dtoList;
    }

    @Transactional(readOnly = true)
    public List<TagResponseDto> getSomeoneTags(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        List<Tag> tagList = tagRepository.findAllByMember(member);
        List<TagResponseDto> dtoList = new ArrayList<>();
        tagList.stream().forEach(i -> dtoList.add(new TagResponseDto().toDto(i)));

        return dtoList;
    }


    @Transactional // 사용자가 많아지면 문제 생길 수 있음
    public void tagUpload(TagUploadRequestDto req, Member member) {
        // 태그 첫 생성
        if (req.getTags().size() > MAX_SIZE) {
            // 태그는 4개까지 선택가능
            throw new TagLimitException();
        }

        if (tagRepository.existsByMember(member)) {
            // 이미 처음에 태그를 선택했다면 쫓겨남 / 태그 수정으로 고쳐야함
            throw new UserCreateTagAlreadyExistsException();
        }

        List<Tag> tags = new ArrayList<>();

        for (int i = 0; i < req.getTags().size(); i++) {
            Tag tag = new Tag(req.getTags().get(i), member);
            tags.add(tag);
        }

        tagRepository.saveAll(tags);
    }

    @Transactional
    public List<TagResponseDto> editTags(TagEditRequestDto req, Member member) {
        if (!tagRepository.existsByMember(member)) {
            throw new TagIsEmptyException();
        }

        List<Tag> tagList = tagRepository.findAllByMember(member);

        if(tagList.size() - req.getDeletedTags().size() + req.getAddedTags().size() > MAX_SIZE || tagList.size() - req.getDeletedTags().size() + req.getAddedTags().size() < MIN_SIZE ) {
            throw new TagLimitException();
        }

        List<Tag> deletedTags = req.getDeletedTags();
        List<Tag> addedTags = req.getAddedTags();

        for(int i=0; i<deletedTags.size(); i++) {
            if(!tagRepository.existsTagByMemberAndNameContaining(member, deletedTags.get(i).getName())) {
                throw new TagAlreadyExistException();
            }
            tagRepository.deleteTagByMemberAndNameContaining(member, deletedTags.get(i).getName());
        }

        for(int i=0; i<addedTags.size(); i++) {
            if(tagRepository.existsTagByMemberAndNameContaining(member, addedTags.get(i).getName())) {
                throw new TagAlreadyExistException();
            }
            Tag tag = new Tag(addedTags.get(i).getName(), member);
            tagRepository.save(tag);
        }

        List<Tag> nowTagList = tagRepository.findAllByMember(member);
        List<TagResponseDto> dtoList = new ArrayList<>();
        nowTagList.stream().forEach(i -> dtoList.add(new TagResponseDto().toDto(i)));
        return dtoList;
    }
}
