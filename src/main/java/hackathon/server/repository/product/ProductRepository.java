package hackathon.server.repository.product;

import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAll(Pageable pageable);

//    // JPQL IN
//    @Query("select p from Product p where p.tag in :tags")
//    List<Product> findAllBySelectedTagsIn(String[] tags);

    List<Product> findAllByGuide(Member guide);
}
