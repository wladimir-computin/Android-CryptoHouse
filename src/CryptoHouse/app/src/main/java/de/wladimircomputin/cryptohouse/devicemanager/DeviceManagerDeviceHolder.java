package de.wladimircomputin.cryptohouse.devicemanager;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import de.wladimircomputin.cryptohouse.R;

public class DeviceManagerDeviceHolder extends DeviceManagerHeaderHolder implements ItemTouchHelperViewHolder {

    public final View deviceSettingsView;
    public final ImageView arrowView;
    public final TextView hostnameText;
    public final TextView macText;
    public final Spinner devicetypeSpinner;
    public final EditText nameEdittext;
    public final EditText devpassEdittext;
    public final Button deviceItemScanButton;
    public final CheckBox autoIPCheckbox;
    public final EditText ipEdittext;

    public DeviceManagerDeviceHolder(View itemView, Context context) {
        super(itemView, context);
        deviceSettingsView = itemView.findViewById(R.id.devicemanager_device_settings_layout);
        nameEdittext = itemView.findViewById(R.id.name_edittext);
        arrowView = itemView.findViewById(R.id.devicemanager_arrowView);
        hostnameText = itemView.findViewById(R.id.devicemanager_hostname_textview);
        macText = itemView.findViewById(R.id.devicemanager_mac_textview);
        devicetypeSpinner = itemView.findViewById(R.id.devicetype_spinner);
        ipEdittext = itemView.findViewById(R.id.host_edittext);
        devpassEdittext = itemView.findViewById(R.id.devpass_edittext);
        deviceItemScanButton = itemView.findViewById(R.id.device_item_scan_button);
        autoIPCheckbox = itemView.findViewById(R.id.device_item_ip_auto_checkbox);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.devicetypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        devicetypeSpinner.setAdapter(adapter);
    }
}