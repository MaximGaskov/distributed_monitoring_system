package edu.max.monsys.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "rate_config", schema = "config_and_log_schema")
public class ConfigMonitoringRate {

    @Id
    @Column(name = "id", columnDefinition = "integer default 0", updatable = false, unique = true)
    private Integer id;

    @Column(name = "rate", nullable = false, columnDefinition = "integer default 60")
    private int rateSeconds;

    public ConfigMonitoringRate() {
       id= 0;
       rateSeconds = 60;
    }
}
