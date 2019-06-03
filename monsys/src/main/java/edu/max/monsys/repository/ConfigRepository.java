package edu.max.monsys.repository;

import edu.max.monsys.entity.ConfigMonitoringRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<ConfigMonitoringRate, Integer> {
}
