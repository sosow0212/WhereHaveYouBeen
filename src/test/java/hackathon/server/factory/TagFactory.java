package hackathon.server.factory;

import hackathon.server.entity.member.Member;
import hackathon.server.entity.tag.Tag;

public class TagFactory {

    public static Tag createTag(Member member) {
        return new Tag("a", member);
    }
    public static Tag createTag2(Member member) {
        return new Tag("b", member);
    }
}
