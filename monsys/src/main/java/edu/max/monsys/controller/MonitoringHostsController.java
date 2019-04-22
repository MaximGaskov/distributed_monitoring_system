package edu.max.monsys.controller;

import edu.max.monsys.entity.MonitoringHost;
import edu.max.monsys.repository.MonitoringHostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/hosts/monitoring")
public class MonitoringHostsController {

    private final MonitoringHostRepository monitoringHostRepository;

    public MonitoringHostsController(MonitoringHostRepository monitoringHostRepository) {
        this.monitoringHostRepository = monitoringHostRepository;
    }

    @GetMapping
    public Iterable<MonitoringHost> findAll() {
        return monitoringHostRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Optional<MonitoringHost> findById(@PathVariable("id") Integer id) {
        return monitoringHostRepository.findById(id);
    }
}
