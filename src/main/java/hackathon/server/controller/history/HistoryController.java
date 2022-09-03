package hackathon.server.controller.history;

import hackathon.server.dto.history.ReviewCreateRequestDto;
import hackathon.server.entity.member.Member;
import hackathon.server.exception.MemberNotFoundException;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.response.Response;
import hackathon.server.service.history.HistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "History Controller (for User)" , tags = "history")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class HistoryController {

    private final HistoryService historyService;
    private final MemberRepository memberRepository;

    @ApiOperation(value = "리뷰 작성", notes = "리뷰를 작성합니다.")
    @PostMapping("/histories/{historyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Response create(@ApiParam(value = "history id", required = true) @PathVariable("historyId") Long id, @Valid @RequestBody ReviewCreateRequestDto req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        historyService.create(id, req, member);
        return Response.success();
    }

    @ApiOperation(value = "완료된 전체 거래 내역 조회", notes = "전체 거래 내역을 조회합니다.")
    @GetMapping("/histories")
    @ResponseStatus(HttpStatus.OK)
    public Response findAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        return Response.success(historyService.findAll(member));
    }

    @ApiOperation(value = "거래 내역 단건 조회", notes = "거래 내역을 단건 조회합니다.")
    @GetMapping("/histories/{historyId}")
    @ResponseStatus(HttpStatus.OK)
    public Response find(@ApiParam(value = "history id", required = true) @PathVariable("historyId") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        return Response.success(historyService.find(id, member));
    }

}
