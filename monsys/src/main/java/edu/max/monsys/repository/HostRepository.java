package edu.max.monsys.repository;

import edu.max.monsys.entity.Host;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface HostRepository extends CrudRepository<Host, Integer> {

    Optional<Host> findById(Integer id);
    Optional<Host> findHostByIpAddress(String ip);
}
