package edu.max.repository;

import edu.max.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogRepository extends JpaRepository<Log, Integer> {

    Optional<Log> findLogByHostAndPort(String host, int port);
}
