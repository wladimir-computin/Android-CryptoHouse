package de.wladimircomputin.cryptohouse.actions.status;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.boilerplate.ItemTouchHelperViewHolder;

public class ActionItemHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    public final TextView titleText;
    public final ImageView drag;
    public final Button deleteButton;
    public final Button cloneButton;
    public final Button configButton;
    public final Button actionButton;
    public final ImageView arrow;
    public final View advancedPanel;
    public final RecyclerView actionRecycleview;


    public ActionItemHolder(View itemView) {
        super(itemView);
        titleText = itemView.findViewById(R.id.title_text);
        deleteButton = itemView.findViewById(R.id.delete_button);
        cloneButton = itemView.findViewById(R.id.clone_button);
        actionButton = itemView.findViewById(R.id.action_button);
        configButton = itemView.findViewById(R.id.action_config_button);
        drag = itemView.findViewById(R.id.drag_image);
        arrow = itemView.findViewById(R.id.arrow_view);
        advancedPanel = itemView.findViewById(R.id.action_advanced_panel);
        actionRecycleview = itemView.findViewById(R.id.action_recycleview);
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