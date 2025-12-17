package database.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tasks.Task;
import users.User;

@Repository
public interface TaskJpaRepository extends JpaRepository<Task, Long> {
    List<Task> findByPerformer(User user);

    List<Task> findByCreator(User user);

    List<Task> findDistinctBySubscriptionListContaining(User user);
}
