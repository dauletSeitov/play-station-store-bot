package play.station.bot.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;
    private String name;
    private String price;
    private String actualPrice;
    private String image;

    @ManyToMany
    private Set<Subscriber> subscribers = new HashSet<>();

    private String url;

}
