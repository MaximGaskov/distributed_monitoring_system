package edu.msys.monsys.service;

import edu.msys.monsys.repository.PortRepository;
import edu.msys.monsys.entity.Port;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortService {

    @Autowired
    private PortRepository portRepository;

    public Iterable<Port> findAll() {
        return portRepository.findAll();
    }

    public Port save(Port host) {
        return portRepository.save(host);
    }
}