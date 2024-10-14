package de.wladimircomputin.cryptohouse.devicemanager;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import de.wladimircomputin.libcryptoiot.v2.protocol.DiscoveryDevice;

public class DeviceManagerDevice {
    public String id;
    public String name;
    public String hostname;
    public String ip;
    public String mac;
    public String pass;
    public String key;
    public String key_probe;
    public String type;
    public boolean update_ip;
    public String group;
    public boolean settingsVisible = false;

    public DeviceManagerDevice(String id, String name, String type, String host, String ip, String mac, String pass, boolean update_ip, String group){
        this(id, name, type, host, ip, mac, pass, "", "", update_ip, group);
    }

    public DeviceManagerDevice(String id, String name, String type, String host, String ip, String mac, String pass, String key, String key_probe, boolean update_ip, String group){
        this.id = id;
        this.name = name;
        this.type = type;
        this.hostname = host;
        this.ip = ip;
        this.mac = mac;
        this.pass = pass;
        this.key = key;
        this.key_probe = key_probe;
        this.update_ip =update_ip;
        this.group = group;
    }

    public DeviceManagerDevice(JSONObject jsonobj){
        try {
            id = jsonobj.optString("id", "");
            name = jsonobj.optString("name", "");
            type = jsonobj.optString("type", "");
            hostname = jsonobj.optString("hostname", "");
            ip = jsonobj.optString("ip", "");
            mac = jsonobj.optString("mac", "");
            pass = jsonobj.optString("pass", "");
            key = jsonobj.optString("key", "");
            key_probe = jsonobj.optString("key_probe", "");
            update_ip = jsonobj.optBoolean("update_ip", true);
            group = jsonobj.optString("segment", "");

            String host = jsonobj.optString("host", "");
            if (!host.isEmpty()){
                ip = host;
                hostname = name;
            }
        } catch (Exception x){

        }
    }

    public DeviceManagerDevice(String json){
        try{
            JSONObject jsonobj = new JSONObject(json);
            id = jsonobj.optString("id", "");
            name = jsonobj.optString("name", "");
            type = jsonobj.optString("type", "");
            hostname = jsonobj.optString("hostname", "");
            ip = jsonobj.optString("ip", "");
            mac = jsonobj.optString("mac", "");
            pass = jsonobj.optString("pass", "");
            key = jsonobj.optString("key", "");
            key_probe = jsonobj.optString("key_probe", "");
            update_ip = jsonobj.optBoolean("update_ip", true);
            group = jsonobj.optString("segment", "");

            String host = jsonobj.optString("host", "");
            if (!host.isEmpty()){
                ip = host;
                hostname = name;
            }

        } catch (Exception x){}
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject out = new JSONObject();
        out.put("id", id);
        out.put("name", name);
        out.put("type", type);
        out.put("hostname", hostname);
        out.put("ip", ip);
        out.put("mac", mac);
        out.put("pass", pass);
        out.put("key", key);
        out.put("key_probe", key_probe);
        out.put("update_ip", update_ip);
        out.put("segment", group);
        return out;
    }

    @NonNull
    @Override
    public DeviceManagerDevice clone(){
        return new DeviceManagerDevice(id, name, type, hostname, ip, mac, pass, key, key_probe, update_ip, group);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceManagerDevice that = (DeviceManagerDevice) o;
        return  Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(hostname, that.hostname) &&
                Objects.equals(ip, that.ip) &&
                Objects.equals(mac, that.mac) &&
                Objects.equals(pass, that.pass) &&
                Objects.equals(key, that.key) &&
                Objects.equals(key_probe, that.key_probe) &&
                Objects.equals(update_ip, that.update_ip) &&
                Objects.equals(group, that.group);
    }

    public boolean equalsIgnoreId(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceManagerDevice that = (DeviceManagerDevice) o;
        if(mac.isEmpty() || that.mac.isEmpty()){
            return Objects.equals(name, that.name) &&
                    Objects.equals(hostname, that.hostname) &&
                    Objects.equals(type, that.type);
        } else {
            return Objects.equals(name, that.name) &&
                    Objects.equals(hostname, that.hostname) &&
                    Objects.equals(mac, that.mac) &&
                    Objects.equals(type, that.type);
        }
    }

    public boolean equalsToDiscoveryDevice(DiscoveryDevice o) {
        if (o == null) return false;
        // both devices have a MAC
        if(!mac.isEmpty() && !o.mac.isEmpty()) {
            // MAC matches
            return (Objects.equals(mac, o.mac));
        }
        //fallback for old devices or changed MACs
        return Objects.equals(hostname, o.name) && (Objects.equals(type, o.type) || type.equals("CryptoGeneric"));
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, hostname, mac, type);
    }

    @NonNull
    @Override
    public String toString(){
        return name;
    }
}
