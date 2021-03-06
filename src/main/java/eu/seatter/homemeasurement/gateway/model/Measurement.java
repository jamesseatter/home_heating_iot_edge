package eu.seatter.homemeasurement.gateway.model;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 24/12/2018
 * Time: 11:32
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "measurement")
public class Measurement extends BaseEntity implements Comparable<Measurement>{

    @NotNull
    @Column(name = "measurementtime")
    private LocalDateTime measurementTime;

    @NotNull
    @Column(name = "value")
    private Double value;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "sensor_id", nullable = false)
    @ManyToOne
    private Sensor sensor;

    @Override
    public int compareTo(Measurement o) {
        return super.getId().compareTo(o.getId());
    }
}
