package edu.max.monsys.entity;

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
public class Port {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private Host host;
    @Column(nullable = false)
    private int port;
    @Column(name = "port_is_up")
    private boolean up;
    private String service;

    public Port(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Port{" +
                "id=" + id +
                ", host=" + host +
                ", port=" + port +
                ", up=" + up +
                ", service='" + service + '\'' +
                '}';
    }
}
