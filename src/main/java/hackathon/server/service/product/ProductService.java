package hackathon.server.service.product;

import hackathon.server.dto.product.ProductCreateRequestDto;
import hackathon.server.dto.product.ProductEditRequestDto;
import hackathon.server.dto.product.ProductResponseDto;
import hackathon.server.dto.product.ProductsResponseDto;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import hackathon.server.entity.tag.Tag;
import hackathon.server.exception.MemberNotEqualsException;
import hackathon.server.exception.ProductNotFoundException;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.repository.product.ProductRepository;
import hackathon.server.repository.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final TagRepository tagRepository;

    @Transactional
    public void createProduct(ProductCreateRequestDto req, Member member) {
        Product product = req.toDto(req, member);
        productRepository.save(product);
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

        product.setTitle(req.getTitle());
        product.setContent(req.getContent());
        product.setPlace(req.getPlace());
        product.setPrice(req.getPrice());
        product.setOnline(req.getIsOnline());
    }

    @Transactional
    public void deleteProduct(Long id, Member member) {
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        if (!product.getGuide().equals(member)) {
            throw new MemberNotEqualsException();
        }

        productRepository.delete(product);
    }
}
