package de.wladimircomputin.cryptohouse.actions.config;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.wladimircomputin.libcryptoiot.v2.protocol.api.App;
import de.wladimircomputin.libcryptoiot.v2.protocol.api.Command;
import de.wladimircomputin.libcryptoiot.v2.protocol.api.DeviceAPI;

public class CommandAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private List<String> suggestions;
    private JSONArray system_api;
    private Map<String, JSONArray> app_api;
    private DeviceAPI deviceAPI;

    public CommandAutoCompleteAdapter(Context context, int resource) {
        super(context, resource);
        suggestions = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int index) {
        return suggestions.get(index);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<String> filteredList = new ArrayList<>();

                String input = constraint.toString().toLowerCase();
                String[] command = input.split(":", -1);

                for(App app : deviceAPI.apps){
                    if(command.length == 1){
                        addIfStartsWith(input, app.name + ":", filteredList);
                    } else {
                        if(command[0].equalsIgnoreCase(app.name)) {
                            for (Command c : app.commands) {
                                if (command.length == 2) {
                                    if (c.params.isEmpty()) {
                                        addIfStartsWith(input, app.name + ":" + c.name, filteredList);
                                    } else {
                                        addIfStartsWith(input, app.name + ":" + c.name + ":", filteredList);
                                    }
                                } else {
                                    if(command[1].equalsIgnoreCase(c.name)) {
                                        String params_str = "";
                                        for (int i = 0; i < command.length - 2 && i < c.params.size(); i++) {
                                            if (!command[i + 2].isEmpty()) {
                                                params_str += command[i + 2];
                                                if (i < c.params.size() - 1) {
                                                    params_str += ":";
                                                }
                                            }
                                        }
                                        if (command[command.length - 1].trim().isEmpty()) {
                                            params_str += "<" + c.params.get(command.length-3).name + "|" + c.params.get(command.length-3).datatype + ">";
                                        }
                                        filteredList.add(app.name + ":" + c.name + ":" + params_str);
                                    }
                                }
                            }
                        }
                    }
                }

                for (Command c : deviceAPI.system_commands) {
                    if (command.length == 1) {
                        if (c.params.isEmpty()) {
                            addIfStartsWith(input, c.name, filteredList);
                        } else {
                            addIfStartsWith(input, c.name + ":", filteredList);
                        }
                    } else {
                        if(command[0].equalsIgnoreCase(c.name)) {
                            String params_str = "";
                            for (int i = 0; i < command.length - 1 && i < c.params.size(); i++) {
                                if (!command[i + 1].isEmpty()) {
                                    params_str += command[i + 1];
                                    if (i < c.params.size() - 1) {
                                        params_str += ":";
                                    }
                                }
                            }
                            if (command[command.length - 1].trim().isEmpty()) {
                                params_str += "<" + c.params.get(command.length-2).name + "|" + c.params.get(command.length-2).datatype + ">";
                            }
                            filteredList.add(c.name + ":" + params_str);
                        }
                    }
                }

                filterResults.values = filteredList;
                filterResults.count = filteredList.size();
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    clear();
                    suggestions.clear();
                    suggestions.addAll((List<String>) results.values);
                    addAll((List<String>) results.values);
                    notifyDataSetChanged();
                }
            }
        };
    }

    public void addIfStartsWith(String existing, String suggestion, List<String> list) {
        if (suggestion.toLowerCase().startsWith(existing.toLowerCase()) || existing.length() == 1) {
            list.add(suggestion);
        }
    }


    // Method to update suggestions dynamically
    public void updateAppSuggestions(Map<String, JSONArray> app_api) {
        this.app_api = app_api;
    }

    // Method to update suggestions dynamically
    public void updateSystemSuggestions(JSONArray system_api) {
        this.system_api = system_api;
    }

    public void update(DeviceAPI deviceAPI) {
        this.deviceAPI = deviceAPI;
    }
}
