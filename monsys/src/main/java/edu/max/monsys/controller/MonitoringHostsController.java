package edu.max.monsys.controller;

import edu.max.monsys.entity.MonitoringHost;
import edu.max.monsys.repository.MonitoringHostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/hosts/monitoring")
public class MonitoringHostsController {

    @Autowired
    private MonitoringHostRepository monitoringHostRepository;


    @GetMapping
    public Iterable<MonitoringHost> findAll() {
        return monitoringHostRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Optional<MonitoringHost> findById(@PathVariable("id") Integer id) {
        return monitoringHostRepository.findById(id);
    }

}
