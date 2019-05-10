package edu.max.monsys.controller;

import edu.max.monsys.entity.ConfigMonitoringRate;
import edu.max.monsys.entity.Log;
import edu.max.monsys.entity.MonitoringHost;
import edu.max.monsys.monitoring.MonitoringHandler;
import edu.max.monsys.repository.ConfigRepository;
import edu.max.monsys.repository.LogRepository;
import edu.max.monsys.repository.MonitoringHostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/hosts/monitoring")
public class MonitoringHostsController {


    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private MonitoringHostRepository monitoringHostRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    MonitoringHandler monitoringHandler;

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
            return new ResponseEntity<>("Хост уже есть в списке", HttpStatus.BAD_REQUEST);

        MonitoringHost addedMHost = new MonitoringHost(ip);

        monitoringHostRepository.save(addedMHost);

        monitoringHandler.check();

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
        configRepository.findById(0).get().setRateSeconds(rateVal * 60);
        configRepository.flush();
        }
    }
}
