package edu.max.monsys.controller;

import edu.max.monsys.entity.Host;
import edu.max.monsys.entity.Port;
import edu.max.monsys.repository.HostRepository;
import edu.max.monsys.repository.PortRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/ports")
class PortsController {

    @Autowired
    private PortRepository portRepository;
    @Autowired
    private HostRepository hostRepository;

    @GetMapping(value = "/{host_id}")
    public Iterable<Port> findByHostId(@PathVariable("host_id") Integer id) {

        Optional<Host> h = hostRepository.findById(id);

        return h.<Iterable<Port>>map(Host::getPorts).orElse(null);
    }

    @PostMapping
    public String addByHostId(@RequestParam("port") String portNumber, @RequestParam("hostId") String hostId) {

        if (!portNumber.matches("\\d+"))
            return "Недопустимый номер порта";

        int port = Integer.valueOf(portNumber);
        int hId = Integer.valueOf(hostId);


        if (port < 0 || port > 65535)
            return "Недопустимый номер порта";

        if (hostRepository.findById(hId).get().getPorts().stream()
                .anyMatch(o -> o.getNumber() == port))
            return "Порт уже добавлен";

        Port p = new Port(port);

        hostRepository.findById(hId).get().addPort(p);
        portRepository.save(p);


        return "";
    }

}

