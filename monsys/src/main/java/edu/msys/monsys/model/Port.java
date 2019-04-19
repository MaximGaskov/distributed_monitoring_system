package edu.msys.monsys.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties("ports")
    private Host host;
    @Column(nullable = false)
    private int port;
    @Column(name = "port_is_up", columnDefinition = "boolean default false")
    private boolean up;
    private String service;

    public Port(int port, String service) {
        this.host = host;
        this.port = port;
        this.service = service;
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
