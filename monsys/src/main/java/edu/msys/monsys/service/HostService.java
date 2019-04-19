package edu.msys.monsys.service;

import edu.msys.monsys.data.HostRepository;
import edu.msys.monsys.model.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HostService {

    @Autowired
    HostRepository hostRepository;

    public Iterable<Host> findAll() {
        return hostRepository.findAll();
    }

    public Host findByIpAddress(String ipAddr) {
        return hostRepository.findByIpAddress(ipAddr);
    }

    public Host save(Host host) {
        return hostRepository.save(host);
    }
}

