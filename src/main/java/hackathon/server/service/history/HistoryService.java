package hackathon.server.service.history;

import hackathon.server.dto.history.HistoryResponseDto;
import hackathon.server.dto.history.ReviewCreateRequestDto;
import hackathon.server.entity.history.History;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import hackathon.server.entity.review.Review;
import hackathon.server.exception.AlreadyReviewWritenException;
import hackathon.server.exception.HistoryNotFoundException;
import hackathon.server.exception.MemberNotEqualsException;
import hackathon.server.repository.history.HistoryRepository;
import hackathon.server.repository.product.ProductRepository;
import hackathon.server.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HistoryService {

    private final ReviewRepository reviewRepository;
    private final HistoryRepository historyRepository;

    @Transactional
    public void create(Long historyId, ReviewCreateRequestDto req, Member member) {
        History history = historyRepository.findById(historyId).orElseThrow(HistoryNotFoundException::new);
        Product product = history.getProduct();

        if (history.isReviewed()) {
            throw new AlreadyReviewWritenException();
        }

        Review review = new Review(req.getContent(), product, member, req.getScore());
        reviewRepository.save(review);
        history.writeReview();
    }

    @Transactional(readOnly = true)
    public List<HistoryResponseDto> findAll(Member member) {
        List<History> histories = historyRepository.findAllByUser(member);
        List<HistoryResponseDto> res = new ArrayList<>();
        histories.stream().forEach(i -> res.add(new HistoryResponseDto().toDto(i)));
        return res;
    }

    @Transactional(readOnly = true)
    public HistoryResponseDto find(Long id, Member member) {
        History history = historyRepository.findById(id).orElseThrow(HistoryNotFoundException::new);
        if (!history.getUser().equals(member)) {
            throw new MemberNotEqualsException();
        }
        return new HistoryResponseDto().toDto(history);
    }
}
