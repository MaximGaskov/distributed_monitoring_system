package edu.max.controller;

import edu.max.entity.ConfigMonitoringRate;
import edu.max.entity.MonitoringHost;
import edu.max.repository.ConfigRepository;
import edu.max.repository.MonitoringHostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/hosts/monitoring")
public class MonitoringHostsController {


    private final ConfigRepository configRepository;

    private final MonitoringHostRepository monitoringHostRepository;

    public MonitoringHostsController(ConfigRepository configRepository, MonitoringHostRepository monitoringHostRepository) {
        this.configRepository = configRepository;
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

    @PostMapping
    public ResponseEntity<String> create(@RequestParam String ip) {

        if (!ip.matches(
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"))
            return new ResponseEntity<>("Неверный формат IP-адреса", HttpStatus.BAD_REQUEST);
        else if (monitoringHostRepository.findMonitoringHostByIpAddress(ip).isPresent())
            return new ResponseEntity<>("Узел уже есть в списке", HttpStatus.BAD_REQUEST);

        MonitoringHost addedMHost = new MonitoringHost(ip);

        monitoringHostRepository.save(addedMHost);


        monitoringHostRepository.flush();

        return ResponseEntity.ok("");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        if (monitoringHostRepository.count() == 1)
            return new ResponseEntity<>("Сперва добавьте другой хост мониторинга", HttpStatus.BAD_REQUEST);
        else {
            monitoringHostRepository.deleteById(id);
        }
        return ResponseEntity.ok("");
    }

    @PostMapping("/rate")
    public void setMonitoringRate(@RequestParam int rateVal) {

        if (configRepository.count() == 0)
            configRepository.save(new ConfigMonitoringRate());
        else  {
            Optional<ConfigMonitoringRate> cfmr = configRepository.findById(0);
            if (cfmr.isPresent()) {
                cfmr.get().setRateSeconds(rateVal * 60);
                configRepository.flush();
            }
        }
    }
}
