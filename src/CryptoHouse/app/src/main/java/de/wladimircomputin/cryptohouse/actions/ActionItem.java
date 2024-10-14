package de.wladimircomputin.cryptohouse.actions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import de.wladimircomputin.cryptohouse.actions.config.ActionDeviceItem;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;

public class ActionItem {
    public String id;
    public String name;
    public ActionDeviceItem[] action_device_items;

    public ActionItem(String id, String name, ActionDeviceItem[] action_device_items) {
        this.id = id;
        this.name = name;
        this.action_device_items = action_device_items;
    }

    public ActionItem(JSONObject jsonobj, Map<String, DeviceManagerDevice> deviceManagerItems){
        try {
            id = jsonobj.getString("id");
            name = jsonobj.getString("name");
            action_device_items = toActionDeviceItemArray(jsonobj.optJSONArray("action_device_items"), deviceManagerItems);
        } catch (Exception x){
            x.printStackTrace();
        }
    }

    public ActionItem(String json, Map<String, DeviceManagerDevice> deviceManagerItems){
        try {
            JSONObject jsonobj = new JSONObject(json);
            id = jsonobj.getString("id");
            name = jsonobj.getString("name");
            action_device_items = toActionDeviceItemArray(jsonobj.optJSONArray("action_device_items"), deviceManagerItems);
        } catch (Exception x){}
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject out = new JSONObject();
        out.put("id", id);
        out.put("name", name);
        out.put("action_device_items", toJsonArray(action_device_items));
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionItem that = (ActionItem) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Arrays.equals(action_device_items, that.action_device_items);
    }

    @Override
    public ActionItem clone() {
        return new ActionItem(id, name, action_device_items);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result += Objects.hash(name);
        result = 31 * result + Arrays.hashCode(action_device_items);
        return result;
    }

    private static ActionDeviceItem[] toActionDeviceItemArray(JSONArray array, Map<String, DeviceManagerDevice> deviceManagerItems) {
        if(array==null)
            return new ActionDeviceItem[] {};

        ActionDeviceItem[] arr = new ActionDeviceItem[array.length()];
        try {
            for (int i = 0; i < arr.length; i++) {
                JSONObject jsonObject = array.optJSONObject(i);
                DeviceManagerDevice deviceManagerDevice = deviceManagerItems.get(jsonObject.optString("device_id"));
                arr[i] = new ActionDeviceItem(jsonObject, new CryptCon(deviceManagerDevice.pass, deviceManagerDevice.ip, deviceManagerDevice.key, deviceManagerDevice.key_probe));
            }
        } catch (Exception x){
            x.printStackTrace();
        }
        return arr;
    }

    private static JSONArray toJsonArray(ActionDeviceItem[] array) {
        if(array==null)
            return new JSONArray();

        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < array.length; i++) {
                if(array[i].commands.length > 0) {
                    jsonArray.put(array[i].toJSON());
                }
            }
        } catch (Exception x){
            x.printStackTrace();
        }
        return jsonArray;
    }
}
