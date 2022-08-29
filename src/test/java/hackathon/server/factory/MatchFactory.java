package hackathon.server.factory;

import hackathon.server.entity.matching.Matching;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;

public class MatchFactory {

    public static Matching createMatch(Member user, Member guide, Product product) {
        return new Matching(user, guide, product);
    }
}
