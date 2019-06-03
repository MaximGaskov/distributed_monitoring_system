package edu.max.repository;

import edu.max.entity.Port;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortRepository extends JpaRepository<Port, Integer> {

    Optional<Port> findPortByNumber(int num);//and host id
}
