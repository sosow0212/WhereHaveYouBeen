package hackathon.server.repository.Member;

import hackathon.server.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    public boolean existsByUsername(String username);
    public boolean existsByNickname(String nickname);
    public boolean existsByEmail(String Email);
    public boolean existsByPhone(String phone);

    Optional<Member> findByPhone(String phone);
    Optional<Member> findByNickname(String nickname);

}
