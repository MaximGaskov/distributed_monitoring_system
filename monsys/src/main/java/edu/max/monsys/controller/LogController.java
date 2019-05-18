package edu.max.monsys.controller;

import edu.max.monsys.entity.Log;
import edu.max.monsys.repository.LogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogRepository logRepository;

    public LogController(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @GetMapping
    public Iterable<Log> getLogs() {
        return logRepository.findAll();
    }

    @DeleteMapping
    public ResponseEntity<?> delete() {

        logRepository.deleteAll();

        return ResponseEntity.ok("");
    }
}
