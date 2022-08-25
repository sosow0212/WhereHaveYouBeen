package hackathon.server.controller.tag;

import hackathon.server.dto.tag.TagEditRequestDto;
import hackathon.server.dto.tag.TagUploadRequestDto;
import hackathon.server.entity.member.Member;
import hackathon.server.exception.MemberNotFoundException;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.response.Response;
import hackathon.server.service.tag.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "Tag Controller", tags = "tag")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TagController {

    private final TagService tagService;
    private final MemberRepository memberRepository;

    @ApiOperation(value = "본인 태그 조회", notes = "본인이 선택한 태그를 조회합니다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/tags")
    public Response getOwnTags() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);

        return Response.success(tagService.getOwnTags(member));
    }

    @ApiOperation(value = "다른 사람 태그 조회", notes = "다른 사람이 선택한 태그를 조회합니다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/tags/{id}")
    public Response getSomeoneTags(@PathVariable("id") Long id) {
        return Response.success(tagService.getSomeoneTags(id));
    }


    @ApiOperation(value = "관광객 태그 첫 등록", notes = "첫 태그 4개를 등록합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/tags")
    public Response createTags(@Valid @RequestBody TagUploadRequestDto tagUploadRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);

        tagService.tagUpload(tagUploadRequestDto, member);
        return Response.success();
    }

    @ApiOperation(value = "태그 수정하기", notes = "태그를 수정합니다.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/tags")
    public Response editTags(@Valid @RequestBody TagEditRequestDto tagEditRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        return Response.success(tagService.editTags(tagEditRequestDto, member));
    }

}
