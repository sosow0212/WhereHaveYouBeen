package hackathon.server.controller.member;

import hackathon.server.dto.member.MemberEditRequestDto;
import hackathon.server.entity.member.Member;
import hackathon.server.exception.MemberNotFoundException;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.response.Response;
import hackathon.server.service.member.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "User Controller", tags = "user")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @ApiOperation(value = "로그인된 유저 회원정보 조회", notes = "로그인된 유저의 회원정보를 조회합니다.")
    @GetMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    public Response getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        return Response.success(memberService.getMember(member));
    }

    @ApiOperation(value = "로그인된 유저의 회원정보 수정", notes = "로그인된 유저의 회원정보를 수정합니다.")
    @PutMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    public Response editMember(@Valid @RequestBody MemberEditRequestDto req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        memberService.editMember(req, member);
        return Response.success();
    }

    @ApiOperation(value = "회원탈퇴", notes = "회원탈퇴를 진행합니다.")
    @DeleteMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    public Response deleteMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        memberService.deleteMember(member);
        return Response.success();
    }

    @ApiOperation(value = "진행중인 거래 확인", notes = "진행중인 거래를 확인합니다.")
    @GetMapping("/members/trips")
    @ResponseStatus(HttpStatus.OK)
    public Response getTrips() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        return Response.success(memberService.getTrips(member));
    }
}
