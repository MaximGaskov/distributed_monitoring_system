package edu.max.monsys.controller;

import edu.max.monsys.entity.Log;
import edu.max.monsys.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogRepository logRepository;

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
