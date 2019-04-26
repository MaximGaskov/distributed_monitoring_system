package edu.max.monsys.controller;

import edu.max.monsys.entity.Host;
import edu.max.monsys.repository.HostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public String create(@RequestParam String ip) {
//        Preconditions.checkNotNull(resource);

        if (!ip.matches(
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"))
                return "Неверный формат IP-адреса";

        if (hostRepository.findHostByIpAddress(ip).isPresent())
            return "Хост уже есть в списке";

        hostRepository.save(new Host(ip));

        return "";
    }

}