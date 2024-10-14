package de.wladimircomputin.cryptohouse.devicesettings.DeviceEvents;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class DeviceEvent {
    public String event;
    public String command;

    public DeviceEvent(String event, String command) {
        this.event = event;
        this.command = command;
    }

    public DeviceEvent(JSONObject jsonobj){
        try {
            event = jsonobj.names().optString(0);
            command = jsonobj.optString(jsonobj.names().optString(0));
        } catch (Exception x){}
    }

    public DeviceEvent(String json){
        try {
            JSONObject jsonobj = new JSONObject(json);
            event = jsonobj.names().optString(0);
            command = jsonobj.optString(jsonobj.names().optString(0));
        } catch (Exception x){}
    }

    public String toJSON() throws JSONException {
        JSONObject out = new JSONObject();
        out.put(event, command);
        return out.toString();
    }

    @NonNull
    @Override
    public DeviceEvent clone(){
        return new DeviceEvent(event, command);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceEvent deviceEvent = (DeviceEvent) o;
        return Objects.equals(event, deviceEvent.event) && Objects.equals(command, deviceEvent.command);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, command);
    }
}
