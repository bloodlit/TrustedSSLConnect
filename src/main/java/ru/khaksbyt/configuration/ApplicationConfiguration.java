package ru.khaksbyt.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Slf4j
@Configuration
public class ApplicationConfiguration {
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        log.info("Set default timezone: " + TimeZone.getDefault().getID() + "/" + TimeZone.getDefault().getDisplayName());
    }
}
