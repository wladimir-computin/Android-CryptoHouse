package de.wladimircomputin.libcryptoiot.v2.protocol.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Command {
    public String name;
    public List<Param> params;

    public Command(String name, List<Param> params) {
        this.name = name;
        this.params = params;
    }

    public Command(String name) {
        this(name, new ArrayList<>());
    }

    public Command(String name, JSONObject command){
        try {
            this.name = name;
            params = new ArrayList<>();
            JSONArray param_names = command.optJSONObject(name).names();
            JSONObject param_value = command.optJSONObject(name);
            for (int i = 0; i < param_names.length(); i++) {
                params.add(new Param(param_names.optString(i), param_value));
            }
        } catch (Exception x) {}
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return Objects.equals(name, command.name) && Objects.equals(params, command.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, params);
    }
}
