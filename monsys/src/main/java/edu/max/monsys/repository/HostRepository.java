package edu.max.monsys.repository;

import edu.max.monsys.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HostRepository extends JpaRepository<Host, Integer> {

    Optional<Host> findById(Integer id);
    Optional<Host> findHostByIpAddress(String ip);
}
