package hackathon.server.repository.match;

import hackathon.server.entity.matching.Matching;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchRepository extends JpaRepository<Matching, Long> {

    boolean existsByUserAndProduct(Member user, Product product);
    Optional<Matching> findMatchByUserAndGuide(Member user, Member guide);
}
