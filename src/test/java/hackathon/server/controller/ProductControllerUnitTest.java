package hackathon.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.server.controller.product.ProductController;
import hackathon.server.dto.product.ProductCreateRequestDto;
import hackathon.server.dto.product.ProductEditRequestDto;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.repository.product.ProductRepository;
import hackathon.server.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static hackathon.server.factory.MemberFactory.createGuide;
import static hackathon.server.factory.MemberFactory.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductControllerUnitTest {
    @InjectMocks
    ProductController productController;

    @Mock
    ProductService productService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    ProductRepository productRepository;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @DisplayName("상품 등록")
    public void createProductTest() throws Exception {
        // given
        Member member = createGuide();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ProductCreateRequestDto req = new ProductCreateRequestDto("제목", "내용", "지역", 100, true);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isCreated());

        // then
        verify(productService).createProduct(req, member);
    }

    @Test
    @DisplayName("상품 목록 전체 조회")
    public void findProducts() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
        Page<Product> result = productRepository.findAll(pageable);

        // when, then
        assertThat(result).isEqualTo(null);
    }

    @Test
    @DisplayName("선택한 태그를 바탕으로 상품 검색")
    public void findProductsByTags() throws Exception {
        // given
        Member member = createGuide();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        List<String> values = Arrays.asList("tag1", "tag2", "tag3");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("tags", values);

        // when, then
        mockMvc.perform(
                get("/api/products/search")
                        .params(params)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자 태그 맞춤 상품 조회")
    public void findRecommendsProductTest() throws Exception {
        // given
        Member member = createGuide();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                get("/api/products/recommends")
        ).andExpect(status().isOk());

        // then
        verify(productService).findRecommendsProduct(member);
    }

    @Test
    @DisplayName("상품 상세 조회")
    public void findProduct() throws Exception {
        // given
        Long id = 1L;

        // when
        mockMvc.perform(
                get("/api/products/{id}", id)
        ).andExpect(status().isOk());

        // then
        verify(productService).findProduct(id);
    }

    @Test
    @DisplayName("상품 수정")
    public void editProduct() throws Exception {
        // given
        Long id = 1L;
        Member member = createGuide();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ProductEditRequestDto req = new ProductEditRequestDto("제목2", "내용2", "지역", 100, true);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                put("/api/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isOk());

        // then
        verify(productService).editProduct(id, req, member);
    }

    @Test
    @DisplayName("상품 삭제")
    public void deleteProduct() throws Exception {
        // given
        Long id = 1L;
        Member member = createGuide();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ProductEditRequestDto req = new ProductEditRequestDto("제목2", "내용2", "지역", 100, true);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                delete("/api/products/{id}", id)
        ).andExpect(status().isOk());

        // then
        verify(productService).deleteProduct(id, member);
    }

    @Test
    @DisplayName("상품 좋아요 및 취소")
    public void likeProduct() throws Exception {
        // given
        Long id = 1L;
        Member member = createGuide();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByUsername(authentication.getName())).willReturn(Optional.of(member));

        // when
        mockMvc.perform(
                post("/api/products/{id}/likes", id)
        ).andExpect(status().isOk());

        // then
        verify(productService).likeProduct(id, member);
    }
}
