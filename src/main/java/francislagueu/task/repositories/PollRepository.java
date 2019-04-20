package francislagueu.task.repositories;

import francislagueu.task.models.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    Optional<Poll> findById(Long pollId);

   Page<Poll> findByCreatedAtBy(String userId, Pageable pageable);

    long countByCreatedBy(String userId);
    List<Poll> findByIdIn(List<Long> pollIds);
    List<Poll> findByIdIn(List<Long> pollIds, Sort sort);
}
