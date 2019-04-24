package edu.max.monsys.controller;

import edu.max.monsys.entity.Host;
import edu.max.monsys.entity.Port;
import edu.max.monsys.repository.HostRepository;
import edu.max.monsys.repository.PortRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/ports")
class PortsController {

    private final PortRepository portRepository;
    private final HostRepository hostRepository;

    public PortsController(PortRepository portRepository, HostRepository hostRepository) {
        this.portRepository = portRepository;
        this.hostRepository = hostRepository;
    }

    @GetMapping(value = "/{host_id}")
    public Iterable<Port> findByHostId(@PathVariable("host_id") Integer id) {

        Optional<Host> h = hostRepository.findById(id);

        return h.<Iterable<Port>>map(Host::getPorts).orElse(null);
    }
}

