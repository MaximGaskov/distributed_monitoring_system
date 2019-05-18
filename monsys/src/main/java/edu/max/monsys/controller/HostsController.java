package edu.max.monsys.controller;

import edu.max.monsys.entity.Host;
import edu.max.monsys.entity.Port;
import edu.max.monsys.repository.HostRepository;
import edu.max.monsys.repository.MonitoringHostRepository;
import edu.max.monsys.repository.PortRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/hosts")
class HostsController {

    private final HostRepository hostRepository;

    private final PortRepository portRepository;

    private final MonitoringHostRepository monitoringHostRepository;

    public HostsController(HostRepository hostRepository, PortRepository portRepository, MonitoringHostRepository monitoringHostRepository) {
        this.hostRepository = hostRepository;
        this.portRepository = portRepository;
        this.monitoringHostRepository = monitoringHostRepository;
    }

    @GetMapping
    public Iterable<Host> findAll() {
        return hostRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> create(@RequestParam String ip) {

        if (!ip.matches(
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"))
                return new ResponseEntity<>("Неверный формат IP-адреса", HttpStatus.BAD_REQUEST);
        else if (hostRepository.findHostByIpAddress(ip).isPresent())
            return new ResponseEntity<>("Узел уже есть в списке", HttpStatus.BAD_REQUEST);


        if (monitoringHostRepository.count() == 0)
            return new ResponseEntity<>("Сперва добавьте узел мониторинга", HttpStatus.BAD_REQUEST);
        else {

            Host h = new Host(ip);

            InetAddress addr = null;
            try {
                addr = InetAddress.getByName(ip);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            if (addr != null) {
                String hostname = addr.getCanonicalHostName();
                h.setDomainName(hostname);
            }

            hostRepository.save(h);
        }

        return ResponseEntity.ok("");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        for (Port p : hostRepository.findById(id).get().getPorts())
            portRepository.deleteById(p.getId());
        hostRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}