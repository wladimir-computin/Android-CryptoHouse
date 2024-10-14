package de.wladimircomputin.libcryptoiot.v2.protocol.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class App {
    public String name;
    public List<Command> commands;

    public App(String name, List<Command> commands) {
        this.name = name;
        this.commands = commands;
    }

    public App(String name){
        this(name, new ArrayList<>());
    }

    public App(String name, JSONArray app){
        this.name = name;
        commands = new ArrayList<>();
        for(int i = 0; i<app.length(); i++){
            commands.add(new Command(app.optJSONObject(i).names().optString(0), app.optJSONObject(i)));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        App app = (App) o;
        return Objects.equals(name, app.name) && Objects.equals(commands, app.commands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, commands);
    }
}
