package edu.max.monsys.repository;

import edu.max.monsys.entity.MonitoringHost;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MonitoringHostRepository extends CrudRepository<MonitoringHost, Integer> {
    Optional<MonitoringHost> findMonitoringHostByIpAddress(String ip);
}
