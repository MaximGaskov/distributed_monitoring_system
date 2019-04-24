package edu.max.monsys.controller;

import edu.max.monsys.entity.Host;
import edu.max.monsys.repository.HostRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hosts")
class HostsController {

    private final HostRepository hostRepository;

    public HostsController(HostRepository hostRepository) {
        this.hostRepository = hostRepository;
    }

    @GetMapping
    public Iterable<Host> findAll() {
        return hostRepository.findAll();
    }

}