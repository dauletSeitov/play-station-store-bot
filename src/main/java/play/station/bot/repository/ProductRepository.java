package play.station.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import play.station.bot.model.entities.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("SELECT p FROM Product p JOIN p.subscribers s WHERE s.login = :chatId")
    List<Product> findSubscribedProductsByChatId(@Param("chatId") Long chatId);
}
