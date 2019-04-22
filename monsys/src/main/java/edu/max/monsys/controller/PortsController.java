package edu.max.monsys.controller;

import edu.max.monsys.entity.Port;
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

    public PortsController(PortRepository portRepository) {
        this.portRepository = portRepository;
    }

    @GetMapping(value = "/{id}")
    public Optional<Port> findById(@PathVariable("id") Integer id) {
        return portRepository.findById(id);
    }
}

