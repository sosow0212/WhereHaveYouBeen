package hackathon.server.repository.history;

import hackathon.server.entity.history.History;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {
    Optional<History> findByUserAndAndProduct(Member user, Product product);
    List<History> findAllByUser(Member user);
}
