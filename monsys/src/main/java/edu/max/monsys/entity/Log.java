package edu.max.monsys.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "logs", schema = "config_and_log_schema")
public class Log {

    @Id
    @GeneratedValue
    private Integer id;

    private String date;

    private String host;

    private int port;

    private String event;

    public Log(String date, String host, int port, String event) {
        this.date = date;
        this.host = host;
        this.port = port;
        this.event = event;
    }

}
