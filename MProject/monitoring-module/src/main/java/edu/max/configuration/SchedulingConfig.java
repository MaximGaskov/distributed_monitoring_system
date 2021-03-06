package edu.max.configuration;

import edu.max.monitoring.MonitoringHandler;
import edu.max.repository.ConfigRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    private final ConfigRepository configRepository;

    public SchedulingConfig(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Bean
    public MonitoringHandler monitoringHandler() {
        return new MonitoringHandler();
    }

    @Bean
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(100);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
                () -> monitoringHandler().check(),
                triggerContext -> {
                    Calendar nextExecutionTime =  new GregorianCalendar();
                    Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
                    nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
                    if (configRepository.count() == 0)
                        nextExecutionTime.add(Calendar.MILLISECOND, 15000);
                    else
                        nextExecutionTime.add(Calendar.MILLISECOND,
                                configRepository.findById(0).get().getRateSeconds() * 1000);
                    return nextExecutionTime.getTime();
                }
        );
        System.out.println("good");
    }
}

