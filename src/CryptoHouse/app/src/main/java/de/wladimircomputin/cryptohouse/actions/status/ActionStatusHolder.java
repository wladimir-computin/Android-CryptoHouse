package de.wladimircomputin.cryptohouse.actions.status;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.wladimircomputin.cryptohouse.R;

public class ActionStatusHolder extends RecyclerView.ViewHolder{

    public final TextView titleText;
    public final LinearLayout commandsLinearlayout;


    public ActionStatusHolder(View itemView) {
        super(itemView);
        titleText = itemView.findViewById(R.id.action_device_title);
        commandsLinearlayout = itemView.findViewById(R.id.action_command_linearlayout);
    }
}