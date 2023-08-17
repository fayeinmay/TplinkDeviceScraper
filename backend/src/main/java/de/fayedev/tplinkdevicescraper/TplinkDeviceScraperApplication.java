package de.fayedev.tplinkdevicescraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TplinkDeviceScraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(TplinkDeviceScraperApplication.class, args);
    }

}
