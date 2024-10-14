package de.wladimircomputin.cryptohouse.actions.status;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.actions.ActionCallback;
import de.wladimircomputin.cryptohouse.actions.config.ActionDeviceItem;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;

public class ActionStatusRecyclerListAdapter extends RecyclerView.Adapter<ActionStatusHolder>{

    public final List<ActionDeviceItem> list = new ArrayList<>();
    Context context;
    HashMap<String, DeviceManagerDevice> deviceManagerItems;
    ActionCallback callback;


    public ActionStatusRecyclerListAdapter(ActionCallback callback, HashMap<String, DeviceManagerDevice> deviceManagerItems, Context context) {
        this.context = context;
        this.deviceManagerItems = deviceManagerItems;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ActionStatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_device_status, parent, false);
        return new ActionStatusHolder(view);
    }

    @Override
    public void onBindViewHolder(final ActionStatusHolder holder, int position) {
        holder.commandsLinearlayout.removeAllViews();
        ActionDeviceItem adi =  list.get(holder.getBindingAdapterPosition());
        if(adi != null){
            if (deviceManagerItems.containsKey(adi.device_id)) {
                holder.titleText.setText(deviceManagerItems.get(adi.device_id).name);
                holder.itemView.setOnClickListener(view -> callback.onActionStatusClicked(adi, holder));
                for (String command : adi.commands) {
                    View rootview = LayoutInflater.from(context).inflate(R.layout.action_device_command_status, null);
                    TextView commandText = rootview.findViewById(R.id.action_command_status);
                    commandText.setText(command);
                    holder.commandsLinearlayout.addView(rootview);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void animateHeightTo(@NonNull View view, int height) {
        final int currentHeight = view.getHeight();
        ObjectAnimator animator = ObjectAnimator.ofInt(view, new HeightProperty(), currentHeight, height);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    static class HeightProperty extends Property<View, Integer> {

        public HeightProperty() {
            super(Integer.class, "height");
        }

        @Override public Integer get(View view) {
            return view.getHeight();
        }

        @Override public void set(View view, Integer value) {
            view.getLayoutParams().height = value;
            view.setLayoutParams(view.getLayoutParams());
        }
    }
}

