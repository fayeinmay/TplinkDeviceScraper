package de.fayedev.tplinkdevicescraper.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public record TplinkDeviceData(LocalDateTime lastUpdated, List<TplinkDevice> devices) {
    public TplinkDeviceData {
        devices = Collections.unmodifiableList(devices);
    }
}
