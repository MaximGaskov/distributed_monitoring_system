package edu.msys.monsys.repository;

import edu.msys.monsys.entity.Port;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortRepository extends CrudRepository<Port, Long> {
}
