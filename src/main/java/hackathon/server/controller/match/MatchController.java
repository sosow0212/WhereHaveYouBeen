package hackathon.server.controller.match;

import hackathon.server.entity.member.Member;
import hackathon.server.exception.MemberNotFoundException;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.response.Response;
import hackathon.server.service.match.MatchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "Match Controller", tags = "Tag")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MatchController {

    private final MatchService matchService;
    private final MemberRepository memberRepository;

    // 매칭 보기는 유저 페이지에서 확인

    @ApiOperation(value = "유저의 매칭 신청", tags = "관광객이 매칭을 신청합니다.")
    @PostMapping("/matches/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Response createMatch(@ApiParam(value = "상품 id", required = true) @PathVariable("productId") Long productId) {
        // 추후에는 매칭대기도 고려해보기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        matchService.createMatch(productId, member);
        return Response.success("매칭 완료");
    }

    @ApiOperation(value = "관광객이 거래 완료 요청", tags = "관광객이 거래 완료 요청을 합니다.")
    @PostMapping("/matches/users/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public Response finishedByUser(@ApiParam(value = "매치 id", required = true) @PathVariable("matchId") Long matchId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);

        return Response.success(matchService.finishedByUser(matchId, member));
    }

    @ApiOperation(value = "가이드가 거래 완료 요청", tags = "유저가 거래 완료 요청을 합니다.")
    @PostMapping("/matches/guides/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public Response finishedByGuide(@ApiParam(value = "매치 id", required = true) @PathVariable("matchId") Long matchId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);

        return Response.success(matchService.finishedByGuide(matchId, member));
    }

    @ApiOperation(value = "관광객이 매칭 취소 요청", tags = "관광객이 매칭을 취소 요청합니다.")
    @DeleteMapping("/matches/users/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public Response cancelledMatchByUser(@ApiParam(value = "매치 id", required = true) @PathVariable("matchId") Long matchId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        matchService.cancelledMatchByUser(matchId, member);
        return Response.success("가이드에게 매칭 취소 요청 완료");
    }

    @ApiOperation(value = "가이드가 매칭 취소 요청", tags = "가이드가 매칭을 취소 요청합니다.")
    @DeleteMapping("/matches/guides/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public Response cancelledMatchByGuide(@ApiParam(value = "매치 id", required = true) @PathVariable("matchId") Long matchId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        matchService.cancelledMatchByGuide(matchId, member);
        return Response.success("가이드에게 매칭 취소 요청 완료");
    }

}
