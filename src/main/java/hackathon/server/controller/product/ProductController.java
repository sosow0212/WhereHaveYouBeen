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

@Api(value = "Product Controller", tags = "Product")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;
    private final MemberRepository memberRepository;

    @ApiOperation(value = "상품 등록", tags = "상품을 등록합니다.")
    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Response createProduct(@Valid @RequestBody ProductCreateRequestDto productCreateRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        productService.createProduct(productCreateRequestDto, member);
        return Response.success();
    }

    @ApiOperation(value = "상품 목록 조회", tags = "상품 목록을 조회합니다.")
    @GetMapping("/products")
    @ResponseStatus(HttpStatus.OK)
    public Response findProducts(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // http://localhost:8080/api/products?page=0
        return Response.success(productService.findProducts(pageable));
    }

    @ApiOperation(value = "상품 상세 조회", tags = "상품을 상세 조회합니다.")
    @GetMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response findProduct(@ApiParam(value = "게시글 id", required = true) @PathVariable("id") Long id) {
        return Response.success(productService.findProduct(id));
    }

    @ApiOperation(value = "상품 수정", tags = "상품을 수정합니다.")
    @PutMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response editProduct(@ApiParam(value = "게시글 id", required = true) @PathVariable("id") Long id, @Valid @RequestBody ProductEditRequestDto productEditRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        productService.editProduct(id, productEditRequestDto, member);
        return Response.success();
    }

    @ApiOperation(value = "상품 삭제", tags = "상품을 삭제합니다.")
    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response deleteProduct(@ApiParam(value = "게시글 id", required = true) @PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(authentication.getName()).orElseThrow(MemberNotFoundException::new);
        productService.deleteProduct(id, member);
        return Response.success();
    }
}
