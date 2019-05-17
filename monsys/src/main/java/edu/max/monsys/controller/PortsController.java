package edu.max.monsys.controller;

import edu.max.monsys.entity.Host;
import edu.max.monsys.entity.Port;
import edu.max.monsys.repository.HostRepository;
import edu.max.monsys.repository.PortRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> addByHostId(@RequestParam("port") String portNumber, @RequestParam("hostId") String hostId) {

        if (!portNumber.matches("\\d+"))
            return new ResponseEntity<>("Недопустимый формат порта", HttpStatus.BAD_REQUEST);
        if (!hostId.matches("\\d+"))
            return new ResponseEntity<>("Узел не выбран", HttpStatus.BAD_REQUEST);

        long port = Long.valueOf(portNumber);
        int hId = Integer.valueOf(hostId);


        if (port < 0 || port > 65535)
            return new ResponseEntity<>("Недопустимый номер порта", HttpStatus.BAD_REQUEST);

        else if (hostRepository.findById(hId).get().getPorts().stream()
                .anyMatch(o -> o.getNumber() == port))
            return new ResponseEntity<>("Порт уже добавлен", HttpStatus.BAD_REQUEST);

        Port p = new Port((int)port);

        hostRepository.findById(hId).ifPresent(x->x.addPort(p));
        if (hostRepository.findById(hId).isPresent())
            portRepository.save(p);
        else
            return new ResponseEntity<>("Узел отсутствует", HttpStatus.BAD_REQUEST);


        return ResponseEntity.ok("");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        portRepository.findById(id).ifPresent(x->x.getHost().getPorts().removeIf(y -> y.getId().equals(id)));
        portRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

