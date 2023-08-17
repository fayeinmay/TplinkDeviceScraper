package de.fayedev.tplinkdevicescraper.model;

import java.util.Objects;

public record TplinkDevice(String name, String mac, String ip, ConnectionType type, ConnectionSubType subType,
                           Integer signalStrength) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TplinkDevice device = (TplinkDevice) o;
        return Objects.equals(name, device.name) && Objects.equals(mac, device.mac)
                && Objects.equals(ip, device.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, mac, ip);
    }
}
