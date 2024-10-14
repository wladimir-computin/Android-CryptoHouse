package de.wladimircomputin.cryptohouse.devicemanager;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import de.wladimircomputin.cryptohouse.R;

public class DeviceManagerHeaderHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    public final ImageView drag;
    public final Button deleteButton;
    public final Button cloneButton;

    public DeviceManagerHeaderHolder(View itemView, Context context) {
        super(itemView);
        deleteButton = itemView.findViewById(R.id.delete_button);
        cloneButton = itemView.findViewById(R.id.clone_button);
        drag = itemView.findViewById(R.id.drag_image);
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