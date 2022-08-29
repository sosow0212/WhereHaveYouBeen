package hackathon.server.service;

import hackathon.server.entity.matching.Matching;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.product.Product;
import hackathon.server.repository.match.MatchRepository;
import hackathon.server.repository.product.ProductRepository;
import hackathon.server.service.match.MatchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static hackathon.server.factory.MatchFactory.createMatch;
import static hackathon.server.factory.MemberFactory.createGuide;
import static hackathon.server.factory.MemberFactory.createUser;
import static hackathon.server.factory.ProductFactory.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MatchServiceUnitTest {

    @InjectMocks
    MatchService matchService;

    @Mock
    MatchRepository matchRepository;

    @Mock
    ProductRepository productRepository;

    @Test
    @DisplayName("매치 생성")
    public void createMatchTest() {
        // given
        Long productId = 1L;
        Member user = createUser();
        user.setMoney(10000);
        Member guide = createGuide();
        Product product = createProduct(guide);
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(matchRepository.existsByUserAndProduct(user, product)).willReturn(false);

        // when
        matchService.createMatch(productId, user);

        // then
        verify(matchRepository).save(any());
    }


    @Test
    @DisplayName("관광객의 거래 완료 요청")
    public void finishedByUserTest() {
        // given
        Long matchId = 1L;
        Member user = createUser();
        Member guide = createGuide();
        Product product = createProduct(guide);
        Matching match = createMatch(user, guide, product);
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));

        // when
        String result = matchService.finishedByUser(matchId, user);

        // then
        assertThat(result).isEqualTo(user.getName() + "님이 매칭 종료를 요청했습니다.");
    }


    @Test
    @DisplayName("가이드의 거래 완료 요청")
    public void finishedByGuideTest() {
        // given
        Long matchId = 1L;
        Member user = createUser();
        Member guide = createGuide();
        Product product = createProduct(guide);
        Matching match = createMatch(user, guide, product);
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));

        // when
        String result = matchService.finishedByGuide(matchId, guide);

        // then
        assertThat(result).isEqualTo(guide.getName() + "님이 매칭 종료를 요청했습니다.");
    }


    @Test
    @DisplayName("양쪽 모두 거래 완료 요청 (User 기준)")
    public void finishedAllByUserTest() {
        // given
        Long matchId = 1L;
        Member user = createUser();
        Member guide = createGuide();
        Product product = createProduct(guide);
        Matching match = createMatch(user, guide, product);
        match.finishByGuide();
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));

        // when
        String result = matchService.finishedByUser(matchId, user);

        // then
        assertThat(result).isEqualTo("매칭이 종료되었습니다.");
    }


    @Test
    @DisplayName("양쪽 모두 거래 완료 요청 (Guide 기준)")
    public void finishedAllByGuideTest() {
        // given
        Long matchId = 1L;
        Member user = createUser();
        Member guide = createGuide();
        Product product = createProduct(guide);
        Matching match = createMatch(user, guide, product);
        match.finishByUser();
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));

        // when
        String result = matchService.finishedByGuide(matchId, user);

        // then
        assertThat(result).isEqualTo("매칭이 종료되었습니다.");
    }


    @Test
    @DisplayName("관광객이 거래 취소 요청")
    public void cancelledMatchByUserTest() {
        // given
        Long matchId = 1L;
        Member user = createUser();
        Member guide = createGuide();
        Product product = createProduct(guide);
        Matching match = createMatch(user, guide, product);
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));

        // when
        String result = matchService.cancelledMatchByUser(matchId, user);

        // then
        assertThat(result).isEqualTo(user.getName() + "님이 매칭 종료를 요청했습니다.");
    }


    @Test
    @DisplayName("가이드가 거래 취소 요청")
    public void cancelledMatchByGuideTest() {
        // given
        Long matchId = 1L;
        Member user = createUser();
        Member guide = createGuide();
        Product product = createProduct(guide);
        Matching match = createMatch(user, guide, product);
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));

        // when
        String result = matchService.cancelledMatchByGuide(matchId, guide);

        // then
        assertThat(result).isEqualTo(guide.getName() + "님이 매칭 종료를 요청했습니다.");
    }


    @Test
    @DisplayName("양쪽 모두 거래 취소 요청 (User 기준)")
    public void cancelledMatchAllByUserTest() {
        // given
        Long matchId = 1L;
        Member user = createUser();
        Member guide = createGuide();
        Product product = createProduct(guide);
        Matching match = createMatch(user, guide, product);
        match.cancelMatchByGuide();
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));

        // when
        String result = matchService.cancelledMatchByUser(matchId, user);

        // then
        assertThat(result).isEqualTo("매칭이 종료되었습니다.");
    }

    @Test
    @DisplayName("양쪽 모두 거래 취소 요청 (Guide 기준)")
    public void cancelledMatchAllByGuideTest() {
        // given
        Long matchId = 1L;
        Member user = createUser();
        Member guide = createGuide();
        Product product = createProduct(guide);
        Matching match = createMatch(user, guide, product);
        match.cancelMatchByUser();
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));

        // when
        String result = matchService.cancelledMatchByGuide(matchId, guide);

        // then
        assertThat(result).isEqualTo("매칭이 종료되었습니다.");
    }
}
