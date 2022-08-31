package hackathon.server.service.match;

import hackathon.server.entity.history.History;
import hackathon.server.entity.matching.Matching;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import hackathon.server.exception.*;
import hackathon.server.repository.HistoryRepository;
import hackathon.server.repository.match.MatchRepository;
import hackathon.server.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MatchService {
    private final MatchRepository matchRepository;
    private final ProductRepository productRepository;
    private final HistoryRepository historyRepository;

    @Transactional
    public void createMatch(Long productId, Member user) {
        // 유저의 지불 금액은 임시로 match 안에 저장됨
        // 즉 매칭 신청시 유저의 금액은 빠지지만, 가이드의 금액은 오르지 않음 / 오로지 거래 완료 시에 돈이 들어감
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        Member guide = product.getGuide();

        if (matchRepository.existsByUserAndProduct(user, product)) {
            throw new AlreadyMatchedUserAndProduct();
        }

        if (user.getMoney() < product.getPrice()) {
            throw new UserLackOfMoneyException();
        }

        user.setMoney(user.getMoney() - product.getPrice());
        Matching match = new Matching(user, guide, product);

        matchRepository.save(match);
    }

    @Transactional
    public String finishedByUser(Long matchId, Member user) {
        Matching match = matchRepository.findById(matchId).orElseThrow(MatchingNotFoundException::new);
        match.finishByUser();

        if(match.isFinishedMatch()) {
            Member guide = match.getGuide();
            guide.setMoney(guide.getMoney() + match.getTempMoney());
            matchRepository.delete(match);

            History history = new History(user, guide, match.getProduct());
            historyRepository.save(history);
            return "매칭이 종료되었습니다.";
        }

        return user.getName() + "님이 매칭 종료를 요청했습니다.";
    }

    @Transactional
    public String finishedByGuide(Long matchId, Member guide) {
        Matching match = matchRepository.findById(matchId).orElseThrow(MatchingNotFoundException::new);
        match.finishByGuide();

        if(match.isFinishedMatch()) {
            guide.setMoney(guide.getMoney() + match.getTempMoney());
            matchRepository.delete(match);

            History history = new History(match.getUser(), guide, match.getProduct());
            historyRepository.save(history);
            return "매칭이 종료되었습니다.";
        }

        return guide.getName() + "님이 매칭 종료를 요청했습니다.";
    }

    @Transactional
    public String cancelledMatchByUser(Long matchId, Member user) {
        Matching match = matchRepository.findById(matchId).orElseThrow(MatchingNotFoundException::new);

        if (match.getUser() != user) {
            throw new MemberNotEqualsException();
        }

        match.cancelMatchByUser();

        if (match.isCancelledMatch()) {
            user.setMoney(user.getMoney() + match.getTempMoney());
            // 가이드는 환불금액을 사용하면 안된다. 추후에 금액을 임시 금액으로 리팩토링해야할듯
            matchRepository.delete(match);
            return "매칭이 종료되었습니다.";
        }

        return user.getName() + "님이 매칭 종료를 요청했습니다.";
    }

    @Transactional
    public String cancelledMatchByGuide(Long matchId, Member guide) {
        Matching match = matchRepository.findById(matchId).orElseThrow(MatchingNotFoundException::new);

        if (match.getGuide() != guide) {
            throw new MemberNotEqualsException();
        }

        match.cancelMatchByGuide();

        if (match.isCancelledMatch()) {
            Member user = match.getUser();

            user.setMoney(user.getMoney() + match.getTempMoney());
            matchRepository.delete(match);
            return "매칭이 종료되었습니다.";
        }
        return guide.getName() + "님이 매칭 종료를 요청했습니다.";
    }
}
