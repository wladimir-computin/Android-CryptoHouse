package de.wladimircomputin.cryptohouse.profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ProfileItem {
    public String id;
    public String name;
    public String picture;

    public ProfileItem(String id, String name, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }

    public ProfileItem(JSONObject jsonobj){
        try {
            id = jsonobj.getString("id");
            name = jsonobj.getString("name");
            picture = jsonobj.getString("picture");
        } catch (Exception x){}
    }

    public ProfileItem(String json){
        try {
            JSONObject jsonobj = new JSONObject(json);
            id = jsonobj.getString(("id"));
            name = jsonobj.getString("name");
            picture = jsonobj.getString("picture");
        } catch (Exception x){}
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject out = new JSONObject();
        out.put("id", id);
        out.put("name", name);
        out.put("picture", picture);
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileItem that = (ProfileItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
