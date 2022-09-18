package hackathon.server.service.product;

import hackathon.server.dto.product.ProductCreateRequestDto;
import hackathon.server.dto.product.ProductEditRequestDto;
import hackathon.server.dto.product.ProductResponseDto;
import hackathon.server.dto.product.ProductsResponseDto;
import hackathon.server.entity.likes.Likes;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Image;
import hackathon.server.entity.product.Product;
import hackathon.server.entity.tag.Tag;
import hackathon.server.exception.MemberNotEqualsException;
import hackathon.server.exception.ProductNotFoundException;
import hackathon.server.repository.likes.LikesRepository;
import hackathon.server.repository.product.ProductRepository;
import hackathon.server.repository.tag.TagRepository;
import hackathon.server.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final LikesRepository likesRepository;
    private final FileService fileService;

    @Transactional
    public void createProduct(ProductCreateRequestDto req, Member member) {
        List<Image> images = req.getImages().stream().map(i -> new Image(i.getOriginalFilename())).collect(toList());
        Product product = productRepository.save(new Product(member, req.getTitle(), req.getContent(), req.getPlace(), req.getPrice(), req.getIsOnline(), images));
        uploadImages(product.getImages(), req.getImages());
    }

    @Transactional(readOnly = true)
    public List<ProductsResponseDto> findProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductsResponseDto> res = new ArrayList<>();
        products.stream().forEach(i -> res.add(new ProductsResponseDto().toDto(i)));
        return res;
    }

    @Transactional(readOnly = true)
    public List<ProductsResponseDto> findProductsByTags(List<String> selectedTags, Member member) {
        HashSet<Tag> tagsWithRoleGuide = new HashSet<>();

        for(String tagName : selectedTags) {
            List<Tag> tagTemp = tagRepository.findAllByCheckGuideTrueAndName(tagName);
            tagTemp.stream().forEach(tag -> tagsWithRoleGuide.add(tag));
        }

        List<Product> products = new ArrayList<>();
        for (Tag tagWithRoleGuide : tagsWithRoleGuide) {
            List<Product> temp = productRepository.findAllByGuide(tagWithRoleGuide.getMember());
            temp.forEach(product -> products.add(product));
        }

        List<ProductsResponseDto> res = new ArrayList<>();
        products.forEach(product -> res.add(new ProductsResponseDto().toDto(product)));

        return res;
    }

    @Transactional(readOnly = true)
    public List<ProductsResponseDto> findRecommendsProduct(Member member) {
        List<Tag> tags = member.getTags();
        HashSet<Product> products = new HashSet<>();
        HashSet<Tag> allGuidesTags = new HashSet<>();

        for (Tag userTag : tags) {
            List<Tag> temp = tagRepository.findAllByCheckGuideTrueAndName(userTag.getName());
            temp.forEach(tag -> allGuidesTags.add(tag));
        }

        for (Tag guideTag : allGuidesTags) {
            List<Product> temp = productRepository.findAllByGuide(guideTag.getMember());
            temp.forEach(product -> products.add(product));
        }

        List<ProductsResponseDto> res = new ArrayList<>();
        products.forEach(product -> res.add(new ProductsResponseDto().toDto(product)));

        return res;
    }

    @Transactional(readOnly = true)
    public ProductResponseDto findProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        return new ProductResponseDto().toDto(product);
    }

    @Transactional
    public void editProduct(Long id, ProductEditRequestDto req, Member member) {
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        if (!product.getGuide().equals(member)) {
            throw new MemberNotEqualsException();
        }

        Product.ImageUpdatedResult result = product.update(req);
        uploadImages(result.getAddedImages(), result.getAddedImageFiles());
        deleteImages(result.getDeletedImages());
    }

    @Transactional
    public void deleteProduct(Long id, Member member) {
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        if (!product.getGuide().equals(member)) {
            throw new MemberNotEqualsException();
        }

        productRepository.delete(product);
    }

    @Transactional
    public void likeProduct(Long id, Member member) {
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);

        if(likesRepository.findByMemberAndProduct(member, product).isPresent()) {
            // 이미 값이 존재하면 좋아요 취소
            Likes likes = likesRepository.findByMemberAndProduct(member, product).get();
            likesRepository.delete(likes);
        } else {
            // 좋아요 처리 한 적이 없다면 좋아요 처리
            likesRepository.save(new Likes(member, product));
        }
    }

    private void uploadImages(List<Image> images, List<MultipartFile> fileImages) {
        IntStream.range(0, images.size()).forEach(i -> fileService.upload(fileImages.get(i), images.get(i).getUniqueName()));
    }

    private void deleteImages(List<Image> images) {
        images.stream().forEach(i -> fileService.delete(i.getUniqueName()));
    }
}
