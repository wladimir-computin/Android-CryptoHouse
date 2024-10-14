package de.wladimircomputin.cryptohouse.devicesettings.TimeEvents;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import de.wladimircomputin.libcryptoiot.v2.protocol.api.TimeEventType;

public class TimeEvent {
    public TimeEventType timeEventType;
    public String time;
    public String command;

    public TimeEvent(TimeEventType timeEventType, String time, String command) {
        this.timeEventType = timeEventType;
        this.time = time;
        this.command = command;
    }

    public TimeEvent(JSONObject jsonobj){
        try {
            timeEventType = TimeEventType.fromString(jsonobj.names().optString(0));
            if(timeEventType == TimeEventType.TIME) {
                time = jsonobj.names().optString(0);
            } else {
                time = "00:00";
            }
            command = jsonobj.optString(jsonobj.names().optString(0));
        } catch (Exception x){}
    }

    public TimeEvent(String json){
        try {
            JSONObject jsonobj = new JSONObject(json);
            timeEventType = TimeEventType.fromString(jsonobj.names().optString(0));
            if(timeEventType == TimeEventType.TIME) {
                time = jsonobj.names().optString(0);
            }
            command = jsonobj.optString(jsonobj.names().optString(0));
        } catch (Exception x){}
    }

    public String toJSON() throws JSONException {
        JSONObject out = new JSONObject();
        if (timeEventType == TimeEventType.TIME) {
            out.put(time, command);
        } else {
            out.put(timeEventType.toString(), command);
        }
        return out.toString();
    }

    @NonNull
    @Override
    public TimeEvent clone(){
        return new TimeEvent(timeEventType, time, command);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeEvent timeEvent = (TimeEvent) o;
        return timeEventType == timeEvent.timeEventType && Objects.equals(time, timeEvent.time) && Objects.equals(command, timeEvent.command);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeEventType, time, command);
    }
}
