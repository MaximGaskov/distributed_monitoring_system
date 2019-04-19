package edu.msys.monsys.service;

import edu.msys.monsys.data.PortRepository;
import edu.msys.monsys.model.Port;
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