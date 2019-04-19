package edu.msys.monsys.data;

import edu.msys.monsys.model.Host;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostRepository
        extends CrudRepository<Host, Long> {

    Host findByIpAddress(String ipAddr);
}
