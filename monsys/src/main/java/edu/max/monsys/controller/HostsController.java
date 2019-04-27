package edu.max.monsys.controller;

import edu.max.monsys.entity.Host;
import edu.max.monsys.repository.HostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/hosts")
class HostsController {

    @Autowired
    private HostRepository hostRepository;

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
            return new ResponseEntity<>("Хост уже есть в списке", HttpStatus.BAD_REQUEST);

        hostRepository.save(new Host(ip));

        return ResponseEntity.ok("");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        hostRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}