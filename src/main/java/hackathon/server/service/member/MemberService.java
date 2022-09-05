package hackathon.server.service.member;

import hackathon.server.dto.member.MemberEditRequestDto;
import hackathon.server.dto.member.MemberResponseDto;
import hackathon.server.dto.member.MemberTripsResponseDto;
import hackathon.server.entity.matching.Matching;
import hackathon.server.entity.member.Member;
import hackathon.server.exception.MemberDoesntDeletedByMatchException;
import hackathon.server.exception.MemberNicknameAlreadyExistsException;
import hackathon.server.exception.MemberPhoneAlreadyExistsException;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.repository.match.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {

    public final MemberRepository memberRepository;
    public final MatchRepository matchRepository;

    @Transactional(readOnly = true)
    public MemberResponseDto getMember(Member member) {
        return new MemberResponseDto().toDto(member);
    }

    @Transactional
    public void editMember(MemberEditRequestDto req, Member member) {
        // 중복인 경우 어떻게 처리할 것인지? (unique 속성) / nickname, phone

        if (memberRepository.existsByNickname(req.getNickname())) {
            if (!req.getNickname().equals(member.getNickname())) {
                throw new MemberNicknameAlreadyExistsException(req.getNickname());
            }
        }

        if (memberRepository.existsByPhone(req.getPhone())) {
            if (!req.getPhone().equals(member.getPhone())) {
                throw new MemberPhoneAlreadyExistsException(req.getPhone());
            }
        }

        member.setName(req.getName());
        member.setNickname(req.getNickname());
        member.setPhone(req.getPhone());
        member.setAddress(req.getAddress());
    }

    @Transactional
    public void deleteMember(Member member) {
        List<Matching> matchings = matchRepository.findAllByUserAndFinishedFalse(member);
        if (!matchings.isEmpty()) {
            throw new MemberDoesntDeletedByMatchException();
        }
        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public List<MemberTripsResponseDto> getTrips(Member member) {
        List<Matching> memberTrips = matchRepository.findAllByUserAndFinishedFalse(member);
        List<MemberTripsResponseDto> result = new ArrayList<>();

        memberTrips.stream().forEach(i -> result.add(new MemberTripsResponseDto().toDto(i)));
        return result;
    }
}
