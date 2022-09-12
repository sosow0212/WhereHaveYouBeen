package hackathon.server.repository.tag;

import hackathon.server.entity.member.Member;
import hackathon.server.entity.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByMember(Member member);
    boolean existsByMember(Member member);

    boolean existsTagByMemberAndNameContaining(Member member, String name);

    void deleteTagByMemberAndNameContaining(Member member, String name);

    List<Tag> findAllByCheckGuideTrue();
    List<Tag> findAllByCheckGuideTrueAndName(String name);
}
