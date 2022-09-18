package hackathon.server.service;

import hackathon.server.dto.product.ProductCreateRequestDto;
import hackathon.server.dto.product.ProductEditRequestDto;
import hackathon.server.dto.product.ProductResponseDto;
import hackathon.server.dto.product.ProductsResponseDto;
import hackathon.server.entity.likes.Likes;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import hackathon.server.entity.tag.Tag;
import hackathon.server.repository.likes.LikesRepository;
import hackathon.server.repository.product.ProductRepository;
import hackathon.server.repository.tag.TagRepository;
import hackathon.server.service.file.FileService;
import hackathon.server.service.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static hackathon.server.factory.ImageFactory.createImage;
import static hackathon.server.factory.MemberFactory.createGuide;
import static hackathon.server.factory.MemberFactory.createUser;
import static hackathon.server.factory.ProductFactory.createProduct;
import static hackathon.server.factory.ProductFactory.createProductWithImages;
import static hackathon.server.factory.TagFactory.createTag;
import static java.util.stream.Collectors.toList;
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

    @Mock
    LikesRepository likesRepository;

    @Mock
    FileService fileService;

    @Test
    @DisplayName("상품등록")
    public void createProductTest() {
        // given
        ProductCreateRequestDto req = new ProductCreateRequestDto("제목", "내용", "지역", 100, true, List.of(
                new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()),
                new MockMultipartFile("test3", "test3.PNG", MediaType.IMAGE_PNG_VALUE, "test3".getBytes())
        ));


        Member member = createGuide();

        given(productRepository.save(any())).willReturn(createProductWithImages(
                createGuide(), IntStream.range(0, req.getImages().size()).mapToObj(i -> createImage()).collect(toList()))
        );

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
        ProductEditRequestDto req = new ProductEditRequestDto("제목2", "내용2", "지역", 100, true, List.of(
                new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()),
                new MockMultipartFile("test3", "test3.PNG", MediaType.IMAGE_PNG_VALUE, "test3".getBytes())
        ), List.of(1, 2));
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
    @DisplayName("선택한 태그를 바탕으로 상품 검색")
    public void findProductsByTags() {
        // given
        List<String> selectedTags = List.of("a");

        Member user = createUser();
        Member guide = createGuide();

        List<Tag> tagTemp = new ArrayList<>();
        tagTemp.add(createTag(guide));
        guide.setTags(tagTemp);

        List<Product> temp = new ArrayList<>();
        temp.add(createProduct(guide));

        given(tagRepository.findAllByCheckGuideTrueAndName(createTag(guide).getName())).willReturn(tagTemp);
        given(productRepository.findAllByGuide(guide)).willReturn(temp);

        // when
        List<ProductsResponseDto> result = productService.findProductsByTags(selectedTags, user);

        // then
        assertThat(result.size()).isEqualTo(1);
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

    @Test
    @DisplayName("상품 좋아요 및 취소 (좋아요가 없는 경우)")
    public void likeProductTest() {
        // given
        Long id = 1L;
        Member member = createUser();
        Member guide = createGuide();
        Product product = createProduct(guide);

        given(productRepository.findById(id)).willReturn(Optional.of(product));
        given(likesRepository.findByMemberAndProduct(member, product)).willReturn(Optional.empty());

        // when
        productService.likeProduct(id, member);

        // then
        verify(likesRepository).save(new Likes(member, product));
    }

    @Test
    @DisplayName("상품 좋아요 및 취소 (좋아요가 이미 있는 경우)")
    public void likeProductAlreadyLikeExistTest() {
        // given
        Long id = 1L;
        Member member = createUser();
        Member guide = createGuide();
        Product product = createProduct(guide);
        Likes likes = new Likes(member, product);

        given(productRepository.findById(id)).willReturn(Optional.of(product));
        given(likesRepository.findByMemberAndProduct(member, product)).willReturn(Optional.of(likes));

        // when
        productService.likeProduct(id, member);

        // then
        verify(likesRepository).delete(likes);
    }
}
