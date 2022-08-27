package hackathon.server.factory;

import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;

public class ProductFactory {

    public static Product createProduct(Member guide) {
        return new Product(guide, "제목", "내용", "지역", 100, true);
    }
}
