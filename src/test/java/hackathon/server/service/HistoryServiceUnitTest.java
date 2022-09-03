package hackathon.server.service;

import hackathon.server.dto.history.HistoryResponseDto;
import hackathon.server.dto.history.ReviewCreateRequestDto;
import hackathon.server.entity.history.History;
import hackathon.server.entity.member.Member;
import hackathon.server.repository.history.HistoryRepository;
import hackathon.server.repository.review.ReviewRepository;
import hackathon.server.service.history.HistoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static hackathon.server.factory.HistoryFactory.createHistory;
import static hackathon.server.factory.MemberFactory.createGuide;
import static hackathon.server.factory.MemberFactory.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HistoryServiceUnitTest {
    @InjectMocks
    HistoryService historyService;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    HistoryRepository historyRepository;

    @Test
    @DisplayName("리뷰 작성")
    public void createTest() {
        // given
        ReviewCreateRequestDto req = new ReviewCreateRequestDto(3L, "리뷰 내용", 5);
        Member member = createUser();
        History history = createHistory(member, createGuide());

        given(historyRepository.findById(req.getHistoryId())).willReturn(Optional.of(history));

        // when
        historyService.create(req, member);

        // then
        verify(reviewRepository).save(any());
    }

    @Test
    @DisplayName("거래 내역 전체 조회")
    public void findAllTest() {
        // given
        Member member = createUser();
        List<History> histories = new ArrayList<>();
        histories.add(createHistory(member, createGuide()));
        histories.add(createHistory(member, createGuide()));
        given(historyRepository.findAllByUser(member)).willReturn(histories);

        // when
        List<HistoryResponseDto> result = historyService.findAll(member);

        // then
        assertThat(result.size()).isEqualTo(histories.size());
    }

    @Test
    @DisplayName("거래 내역 단건 조회")
    public void findTest() {
        // given
        Long id = 1L;
        Member member = createUser();
        History history = createHistory(member, createGuide());
        given(historyRepository.findById(id)).willReturn(Optional.of(history));

        // when
        HistoryResponseDto result = historyService.find(id, member);

        // then
        assertThat(result.getHistoryId()).isEqualTo(history.getId());
    }
}
