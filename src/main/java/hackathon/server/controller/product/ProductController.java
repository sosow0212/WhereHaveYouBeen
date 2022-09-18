package hackathon.server.controller.product;

import hackathon.server.dto.product.ProductCreateRequestDto;
import hackathon.server.dto.product.ProductEditRequestDto;
import hackathon.server.entity.member.Member;
import hackathon.server.exception.MemberNotFoundException;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.response.Response;
import hackathon.server.service.product.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(value = "Product Controller", tags = "Product")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;
    private final MemberRepository memberRepository;

    @ApiOperation(value = "상품 등록", notes = "상품을 등록합니다.")
    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Response createProduct(@Valid @ModelAttribute ProductCreateRequestDto productCreateRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        productService.createProduct(productCreateRequestDto, member);
        return Response.success();
    }

    @ApiOperation(value = "상품 목록 전체 조회", notes = "상품 목록을 전체 조회합니다.")
    @GetMapping("/products")
    @ResponseStatus(HttpStatus.OK)
    public Response findProducts(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // http://localhost:8080/api/products?page=0
        return Response.success(productService.findProducts(pageable));
    }

    @ApiOperation(value = "태그별 상품 조회", notes = "선택한 태그를 바탕으로 검색합니다.")
    @GetMapping("/products/search")
    @ResponseStatus(HttpStatus.OK)
    public Response findProductsByTags(@RequestParam List<String> tags) {
        // http://localhost:8080/products?tag=tag1,tag2,tag3
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        return Response.success(productService.findProductsByTags(tags, member));
    }

    @ApiOperation(value = "사용자 태그 맞춤 상품 조회", notes = "사용자의 태그를 바탕으로 검색합니다.")
    @GetMapping("/products/recommends")
    @ResponseStatus(HttpStatus.OK)
    public Response findProductsByTags() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        return Response.success(productService.findRecommendsProduct(member));
    }

    @ApiOperation(value = "상품 상세 조회", notes = "상품을 상세 조회합니다.")
    @GetMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response findProduct(@ApiParam(value = "게시글 id", required = true) @PathVariable("id") Long id) {
        return Response.success(productService.findProduct(id));
    }

    @ApiOperation(value = "상품 수정", notes = "상품을 수정합니다.")
    @PutMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response editProduct(@ApiParam(value = "게시글 id", required = true) @PathVariable("id") Long id, @Valid @ModelAttribute ProductEditRequestDto productEditRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        productService.editProduct(id, productEditRequestDto, member);
        return Response.success();
    }

    @ApiOperation(value = "상품 삭제", notes = "상품을 삭제합니다.")
    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response deleteProduct(@ApiParam(value = "게시글 id", required = true) @PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        productService.deleteProduct(id, member);
        return Response.success();
    }

    @ApiOperation(value = "상품 좋아요 및 취소", notes = "상품을 좋아요 및 취소 처리를합니다.")
    @PostMapping("/products/{id}/likes")
    @ResponseStatus(HttpStatus.OK)
    public Response likeProduct(@ApiParam(value = "게시글 id", required = true) @PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        productService.likeProduct(id, member);
        return Response.success();
    }
}
