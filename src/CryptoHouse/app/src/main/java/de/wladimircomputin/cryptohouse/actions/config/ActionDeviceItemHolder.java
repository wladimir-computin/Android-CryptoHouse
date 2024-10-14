package de.wladimircomputin.cryptohouse.actions.config;

import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.recyclerview.widget.RecyclerView;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.boilerplate.ItemTouchHelperViewHolder;

public class ActionDeviceItemHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    public final Spinner deviceSpinner;
    public final Button deleteButton;
    public final RecyclerView commandsRecycleview;
    public final Button addButton;


    public ActionDeviceItemHolder(View itemView) {
        super(itemView);
        deviceSpinner = itemView.findViewById(R.id.device_spinner);
        deleteButton = itemView.findViewById(R.id.delete_button);
        commandsRecycleview = itemView.findViewById(R.id.commands_recycleview);
        addButton = itemView.findViewById(R.id.add_button);
    }

    @Override
    public void onItemSelected() {
        itemView.animate().scaleX(1.05f);
        itemView.animate().scaleY(1.05f);
        itemView.animate().alpha(0.9f);
    }

    @Override
    public void onItemClear() {
        itemView.animate().alpha(1);
        itemView.animate().scaleX(1);
        itemView.animate().scaleY(1);
    }
    
}