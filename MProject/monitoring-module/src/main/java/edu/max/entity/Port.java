package edu.max.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "ports", schema = "monitoring_schema")
@org.hibernate.annotations.Entity(dynamicInsert = true)
public class Port {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;
    @Column(nullable = false)
    private int number;
    @Column(name = "port_is_up")
    private boolean up;
    @Column(name = "service", columnDefinition = "varchar(255) default 'неизвестен'")
    private String service;
    @ManyToOne
    @JoinColumn(name="host_id")
    @JsonIgnore
    private Host host;

    public Port(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Port{" +
                "id=" + id +
                ", portNum=" + number +
                ", up=" + up +
                ", service='" + service + '\'' +
                '}';
    }
}
