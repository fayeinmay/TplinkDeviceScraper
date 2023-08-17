package de.fayedev.tplinkdevicescraper.web;

import de.fayedev.tplinkdevicescraper.model.TplinkDeviceData;
import de.fayedev.tplinkdevicescraper.service.TplinkDeviceScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TplinkDeviceScraperRestController {

    private final TplinkDeviceScraperService tplinkDeviceScraperService;

    @GetMapping("/devices")
    public TplinkDeviceData getConnectedDevices() {
        return tplinkDeviceScraperService.getDeviceData();
    }
}
