package edu.max.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "monitoring_hosts", schema = "monitoring_schema")
public class MonitoringHost {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    Integer id;

    @Column(name = "ip_addr", nullable = false, unique = true)
    private String ipAddress;

    @Column(name = "another_mh_ip_addr")
    private String anotherMHIpAdress;

    @Column(name = "is_up", columnDefinition = "boolean default false")
    private boolean up;

    @OneToMany
    @JoinColumn(name = "observer_id")
    private Set<Host> targets = new HashSet<>();

    public MonitoringHost(String ipAddress) {
        this.ipAddress = ipAddress;
    }


}