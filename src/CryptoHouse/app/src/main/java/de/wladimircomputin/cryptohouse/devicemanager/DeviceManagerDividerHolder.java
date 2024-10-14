package de.wladimircomputin.cryptohouse.devicemanager;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import de.wladimircomputin.cryptohouse.R;

public class DeviceManagerDividerHolder extends DeviceManagerHeaderHolder implements ItemTouchHelperViewHolder {
    public final EditText nameEdittext;

    public DeviceManagerDividerHolder(View itemView, Context context) {
        super(itemView, context);
        nameEdittext = itemView.findViewById(R.id.name_edittext);
    }
}