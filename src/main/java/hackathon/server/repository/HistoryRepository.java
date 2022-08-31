package hackathon.server.repository;

import hackathon.server.entity.history.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
