package de.wladimircomputin.libcryptoiot.v2.protocol;

import static de.wladimircomputin.libcryptoiot.v2.Constants.ciot_v2_message_header;

import java.util.Objects;


public class DiscoveryDevice {
    public String type;
    public String name;
    public String mac;
    public String ip;

    public DiscoveryDevice(String type, String name, String mac, String ip){
        this.type = type;
        this.name = name;
        this.mac = mac;
        this.ip = ip;
    }

    public DiscoveryDevice(){
        this("", "", "", "");
    }

    public DiscoveryDevice(String raw){
        try {
            raw = raw.replace(ciot_v2_message_header, "");
            String[] parts = raw.split(":");
            if(parts.length == 3) {
                this.type = parts[0];
                this.name = parts[1];
                this.mac = parts[2];
                this.ip = "";
            } else if (parts.length == 2){
                this.type = parts[0];
                this.name = parts[1];
                this.ip = "";
                this.mac = "";
            }
        } catch (Exception x){
            this.type = "";
            this.name = "";
            this.ip = "";
            this.mac = "";
        }

    }

    public boolean isEmpty(){
        return this.type.isEmpty() && this.name.isEmpty() && this.mac.isEmpty() && this.ip.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscoveryDevice that = (DiscoveryDevice) o;
        return Objects.equals(type, that.type) && Objects.equals(name, that.name) && Objects.equals(mac, that.mac) && Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, mac, ip);
    }
}
