package edu.max.monsys.repository;

import edu.max.monsys.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogRepository extends JpaRepository<Log, Integer> {

    Optional<Log> findLogByHostAndPort(String host, int port);
}
