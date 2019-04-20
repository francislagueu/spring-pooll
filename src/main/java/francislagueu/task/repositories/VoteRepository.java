package francislagueu.task.repositories;

import francislagueu.task.models.ChoiceVoteCount;
import francislagueu.task.models.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("SELECT NEW francislagueu.task.models.ChoiceVoteCount(v.choice.id, count(v.id)) FROM Vote v WHERE v.poll.id in :pollIds GROUP BY v.choice.id")
    List<ChoiceVoteCount> countByPollIdInGroupByChoiceId(@Param("pollIds")List<Long> pollIds);

    @Query("SELECT NEW francislagueu.task.models.ChoiceVoteCount(v.choice.id, count(v.id)) FROM Vote v WHERE v.poll.id = :pollId GROUP BY v.choice.id")
    List<ChoiceVoteCount> countByPollIdGroupByChoiceId(@Param("pollId")Long pollId);

    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.poll.id IN :pollIds")
    List<Vote> findByUserIdAndPollIdIn(@Param("userId")String userId, @Param("pollIds")List<Long> pollIds);

    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.poll.id = :pollId")
    Vote findByUserIdAndPollId(@Param("userId")String userId, @Param("pollId") Long pollId);

    @Query("SELECT COUNT(v.id) FROM Vote v WHERE v.user.id = :userId")
    long countByUserId(@Param("userId")String userId);

    @Query("SELECT v.poll.id FROM Vote v WHERE v.user.id = :userId")
    Page<Long> findVotedPollIdsByUserId(@Param("userId")String userId, Pageable pageable);
}
