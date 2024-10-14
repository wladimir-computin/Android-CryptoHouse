package de.wladimircomputin.libcryptoiot.v2.protocol.api;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.wladimircomputin.libcryptoiot.v2.protocol.Content;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConBulkReceiver;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConReceiver;

public class DeviceAPI {
    public String name;
    public List<App> apps;
    public List<Command> system_commands;

    public DeviceAPI(String name) {
        this.name = name;
        apps = new ArrayList<>();
        system_commands = new ArrayList<>();
    }

    public void generate(CryptCon cc, CryptConReceiver receiver){
        if(cc != null) {
            cc.sendMessageEncrypted("apps", new CryptConReceiver() {
                @Override
                public void onSuccess(Content response) {
                    try {
                        List<String> commands = new ArrayList<>();
                        JSONArray jsonArray_apps = new JSONArray(response.data);
                        commands.add("api");
                        for (int i = 0; i < jsonArray_apps.length(); i++) {
                            commands.add("api" + ":" + jsonArray_apps.optJSONObject(i).names().optString(0));
                        }

                        cc.sendMessageEncryptedBulk(commands.toArray(new String[0]), new CryptConBulkReceiver() {
                            @Override
                            public void onSuccess(Content response, int i) {
                                try {
                                    if (i == 0) {
                                        JSONArray jsonArray = new JSONArray(response.data);
                                        for(int y = 0; y<jsonArray.length(); y++) {
                                            system_commands.add(new Command(jsonArray.optJSONObject(y).names().optString(0), jsonArray.optJSONObject(y)));
                                        }
                                    } else {
                                        apps.add(new App(commands.get(i).split(":")[1], new JSONArray(response.data)));
                                    }
                                    if(i == commands.size()-1){
                                        receiver.onSuccess(null);
                                    }
                                } catch (Exception x) {
                                }
                            }

                            @Override
                            public void onFail(int i) {
                                receiver.onFail();
                            }

                            @Override
                            public void onFinished() {
                                receiver.onFinished();
                            }

                            @Override
                            public void onProgress(String sprogress, int iprogress) {

                            }
                        });

                    } catch (Exception x) {
                    }
                }

                @Override
                public void onFail() {

                }

                @Override
                public void onFinished() {

                }

                @Override
                public void onProgress(String sprogress, int iprogress) {

                }
            });
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceAPI deviceAPI = (DeviceAPI) o;
        return Objects.equals(name, deviceAPI.name) && Objects.equals(apps, deviceAPI.apps) && Objects.equals(system_commands, deviceAPI.system_commands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, apps, system_commands);
    }

    public boolean isEmpty(){
        return system_commands.isEmpty();
    }
}
