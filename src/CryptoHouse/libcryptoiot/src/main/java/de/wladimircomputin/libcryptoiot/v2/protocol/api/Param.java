package de.wladimircomputin.libcryptoiot.v2.protocol.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class Param {
    public String name;
    public Datatype datatype;
    public boolean optional = false;
    public int min = Integer.MIN_VALUE;
    public int max = Integer.MAX_VALUE;

    public Param(String name, Datatype datatype, boolean optional, int min, int max) {
        this.name = name;
        this.datatype = datatype;
        this.optional = optional;
        this.min = min;
        this.max = max;
    }

    public Param(String name, JSONObject param){
        try {
            this.name = name;
            datatype = Datatype.fromString(param.optJSONObject(name).optString("type", ""));
            optional = param.optJSONObject(name).optBoolean("optional", optional);
            min = param.optJSONObject(name).optInt("min", min);
            max = param.optJSONObject(name).optInt("max", max);
        } catch (Exception x){}
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Param param = (Param) o;
        return optional == param.optional && min == param.min && max == param.max && Objects.equals(name, param.name) && datatype == param.datatype;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, datatype, optional, min, max);
    }
}
