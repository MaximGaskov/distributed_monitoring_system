package edu.max.repository;

import edu.max.entity.MonitoringHost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonitoringHostRepository extends JpaRepository<MonitoringHost, Integer> {
    Optional<MonitoringHost> findMonitoringHostByIpAddress(String ip);
    Optional<MonitoringHost> findMonitoringHostByAnotherMHIpAdress(String ip);

}
