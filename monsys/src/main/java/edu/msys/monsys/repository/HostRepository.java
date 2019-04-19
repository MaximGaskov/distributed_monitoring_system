package edu.msys.monsys.repository;

import edu.msys.monsys.entity.Host;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostRepository
        extends CrudRepository<Host, Long> {

    Host findByIpAddress(String ipAddr);
}
