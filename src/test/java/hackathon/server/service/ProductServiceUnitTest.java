package hackathon.server.service;

import hackathon.server.dto.product.ProductCreateRequestDto;
import hackathon.server.dto.product.ProductEditRequestDto;
import hackathon.server.dto.product.ProductResponseDto;
import hackathon.server.dto.product.ProductsResponseDto;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import hackathon.server.entity.tag.Tag;
import hackathon.server.repository.product.ProductRepository;
import hackathon.server.repository.tag.TagRepository;
import hackathon.server.service.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static hackathon.server.factory.MemberFactory.createGuide;
import static hackathon.server.factory.MemberFactory.createUser;
import static hackathon.server.factory.ProductFactory.createProduct;
import static hackathon.server.factory.TagFactory.createTag;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {
    @InjectMocks
    ProductService productService;

    @Mock
    ProductRepository productRepository;

    @Mock
    TagRepository tagRepository;

    @Test
    @DisplayName("상품등록")
    public void createProductTest() {
        // given
        ProductCreateRequestDto req = new ProductCreateRequestDto("제목", "내용", "지역", 100, true);
        Member member = createGuide();
        Product product = req.toDto(req, member);

        // when
        productService.createProduct(req, member);

        // then
        verify(productRepository).save(any());
    }

    @Test
    @DisplayName("상품 수정")
    public void editProductTest() {
        // given
        Long id = 1L;
        ProductEditRequestDto req = new ProductEditRequestDto("제목2", "내용2", "지역", 100, true);
        Member member = createGuide();
        Product product = createProduct(member);
        given(productRepository.findById(id)).willReturn(Optional.of(product));

        // when
        productService.editProduct(id, req, member);

        // then
        assertThat(productRepository.findById(id).get().getContent()).isEqualTo("내용2");
    }

    @Test
    @DisplayName("상품 목록 조회")
    public void findProductsTest() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
        List<Product> products = productRepository.findAll();
        products.add(createProduct(createGuide()));
        Page<Product> res = new PageImpl<>(products);
        given(productRepository.findAll(pageable)).willReturn(res);

        // when
        List<ProductsResponseDto> result = productService.findProducts(pageable);

        // then
        assertThat(result.get(0).getTitle()).isEqualTo(createProduct(createGuide()).getTitle());
    }

    @Test
    @DisplayName("사용자 태그 맞춤 상품 조회")
    public void findRecommendsProductTest() {
        // given
        Member user = createUser();
        Member guide = createGuide();

        List<Tag> userTags = new ArrayList<>();
        userTags.add(createTag(user));
        user.setTags(userTags);

        List<Tag> temp = new ArrayList<>();
        temp.add(createTag(guide));

        List<Product> products = new ArrayList<>();
        products.add(createProduct(guide));

        given(tagRepository.findAllByCheckGuideTrueAndName(createTag(guide).getName())).willReturn(temp);
        given(productRepository.findAllByGuide(guide)).willReturn(products);

        // when
        List<ProductsResponseDto> result = productService.findRecommendsProduct(user);

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("상품 상세 조회")
    public void findProductTest() {
        // given
        Long id = 1L;
        Member member = createGuide();
        Product product = createProduct(member);
        given(productRepository.findById(id)).willReturn(Optional.of(product));

        // when
        ProductResponseDto result = productService.findProduct(id);

        // then
        verify(productRepository).findById(anyLong());
        assertThat(result.getGuide().getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("상품 삭제")
    public void deleteProductTest() {
        // given
        Long id = 1L;
        Member member = createGuide();
        Product product = createProduct(member);
        given(productRepository.findById(id)).willReturn(Optional.of(product));

        // when
        productService.deleteProduct(id, member);

        // then
        verify(productRepository).delete(any());
    }
}
