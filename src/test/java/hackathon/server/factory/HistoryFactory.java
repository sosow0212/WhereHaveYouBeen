package hackathon.server.factory;

import hackathon.server.entity.history.History;
import hackathon.server.entity.member.Member;

import static hackathon.server.factory.ProductFactory.createProduct;

public class HistoryFactory {

    public static History createHistory(Member user, Member guide) {
        return new History(user, guide, createProduct(guide));
    }
}
