package edu.max.monsys.temp;

import edu.max.monsys.repository.HostRepository;
import edu.max.monsys.repository.MonitoringHostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class InitDB {

    @Bean
    CommandLineRunner initDatabase(MonitoringHostRepository mhs, HostRepository hs) {


        return args -> {


        };


    }
}