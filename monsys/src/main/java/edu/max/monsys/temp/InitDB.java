package edu.max.monsys.temp;

import edu.max.monsys.entity.Host;
import edu.max.monsys.entity.MonitoringHost;
import edu.max.monsys.entity.Port;
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

            Host h1 = new Host("127.0.0.1");
            Host h2 = new Host("127.0.0.2");
            Host h3 = new Host("127.0.0.3");

            h1.addPort(new Port(10));
            h1.addPort(new Port(12));

            MonitoringHost mh1 = new MonitoringHost("10.0.0.1");
            mh1.addTarget(h1);
            mh1.addTarget(h2);


            MonitoringHost mh2 = new MonitoringHost("10.0.0.2");
            mh2.addTarget(h3);

            mhs.save(mh1);
            mhs.save(mh2);

        };


    }
}