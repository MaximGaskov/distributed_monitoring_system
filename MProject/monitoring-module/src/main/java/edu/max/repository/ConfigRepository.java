package edu.max.repository;


import edu.max.entity.ConfigMonitoringRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<ConfigMonitoringRate, Integer> {
}
