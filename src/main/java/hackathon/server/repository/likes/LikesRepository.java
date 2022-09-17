package hackathon.server.repository.likes;

import hackathon.server.entity.likes.Likes;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByMemberAndProduct(Member member, Product product);
    List<Likes> findAllByMember(Member member);
}
