package edu.msys.monsys.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Host {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "ip_addr", nullable = false, unique = true)
    private String ipAddress;

    @Column(name = "domain")
    private String domainName;

    @Column(name = "host_is_up", columnDefinition = "boolean default false")
    private boolean up;

    @Column(name = "host_observer")
    private String hostObserver;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "host")
    @JsonIgnoreProperties("host")
    private Set<Port> ports = new HashSet<>();


    public Host(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Host(String ipAddress, String domainName) {
        this.ipAddress = ipAddress;
        this.domainName = domainName;
    }

    public Host(String ipAddress, String domainName, String hostObserver) {
        this.ipAddress = ipAddress;
        this.domainName = domainName;
        this.hostObserver = hostObserver;
    }

    public void addPort(Port port) {
        this.ports.add(port);
        port.setHost(this);
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
