package play.station.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import play.station.bot.model.entities.Subscriber;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscriber, String> {
}
