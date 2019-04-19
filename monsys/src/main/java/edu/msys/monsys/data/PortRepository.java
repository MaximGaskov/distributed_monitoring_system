package edu.msys.monsys.data;

import edu.msys.monsys.model.Port;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortRepository extends CrudRepository<Port, Long> {
}
