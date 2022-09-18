package hackathon.server.factory;

import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Image;
import hackathon.server.entity.product.Product;

import java.util.ArrayList;
import java.util.List;

import static hackathon.server.factory.ImageFactory.createImage;

public class ProductFactory {

    public static Product createProduct(Member guide) {
        List<Image> images = new ArrayList<>();
        images.add(createImage());
        return new Product(guide, "제목", "내용", "지역", 100, true, images);
    }

    public static Product createProductWithImages(Member guide, List<Image> images) {
        return new Product(guide, "제목", "내용", "지역", 100, true, images);
    }
}
