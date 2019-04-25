package edu.max.monsys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "hosts", schema = "monitoring_schema")
@org.hibernate.annotations.Entity(dynamicInsert = true)
public class Host {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(name = "ip_addr", nullable = false, unique = true)
    private String ipAddress;

    @Column(name = "domain", columnDefinition = "varchar(255) default 'неизвестно'")
    private String domainName;

    @Column(name = "host_is_up")
    private boolean up;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="host_id")
    private Set<Port> ports = new HashSet<>();


    public Host(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void addPort(Port port) {
        this.ports.add(port);
    }

    @Override
    public String toString() {
        return "Host{" +
                "id=" + id +
                ", ipAddress='" + ipAddress + '\'' +
                ", domainName='" + domainName + '\'' +
                ", up=" + up +
                '}';
    }
}