package de.wladimircomputin.cryptohouse.devicesettings.DeviceEvents;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.actions.config.CommandAutoCompleteAdapter;
import de.wladimircomputin.cryptohouse.boilerplate.ItemTouchHelperAdapter;
import de.wladimircomputin.cryptohouse.boilerplate.OnStartDragListener;
import de.wladimircomputin.libcryptoiot.v2.protocol.api.DeviceAPI;

public class DeviceEventRecyclerListAdapter extends RecyclerView.Adapter<DeviceEventHolder> implements ItemTouchHelperAdapter {

    public final List<DeviceEvent> list;
    private final OnStartDragListener mDragStartListener;
    private CommandAutoCompleteAdapter commandAutoCompleteAdapter;
    private ItemTouchHelper mItemTouchHelper;

    Context context;

    public DeviceEventRecyclerListAdapter(OnStartDragListener dragStartListener, List<DeviceEvent> deviceEvents, DeviceAPI deviceAPI, Context context) {
        mDragStartListener = dragStartListener;
        this.list = deviceEvents;
        this.context = context;
        commandAutoCompleteAdapter =  new CommandAutoCompleteAdapter(context, R.layout.support_simple_spinner_dropdown_item);
        commandAutoCompleteAdapter.update(deviceAPI);
    }

    @NonNull
    @Override
    public DeviceEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_events_item, parent, false);
        return new DeviceEventHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeviceEventHolder holder, int position) {
        holder.deleteButton.setOnClickListener(v -> {
            onItemRemove(holder.getBindingAdapterPosition());
        });
        holder.cloneButton.setOnClickListener(v -> {
            onItemClone(holder.getBindingAdapterPosition());
        });

        holder.deviceEventsEventEdittext.setText(list.get(holder.getBindingAdapterPosition()).event);
        holder.deviceEventsEventEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                list.get(holder.getBindingAdapterPosition()).event = editable.toString();
            }
        });



        holder.deviceEventsCommandEdittext.setText(list.get(holder.getBindingAdapterPosition()).command);
        holder.deviceEventsCommandEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                list.get(holder.getBindingAdapterPosition()).command = editable.toString();
            }
        });
        holder.deviceEventsCommandEdittext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutoCompleteTextView v = holder.deviceEventsCommandEdittext;
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.showDropDown();
                    }
                },100);
                v.setText(v.getText().toString());
                v.setSelection(v.getText().length());
            }
        });
        holder.deviceEventsCommandEdittext.setOnClickListener((v) -> {
            holder.deviceEventsCommandEdittext.showDropDown();
        });
        holder.deviceEventsCommandEdittext.setThreshold(0);
        holder.deviceEventsCommandEdittext.setAdapter(commandAutoCompleteAdapter);
    }

    @Override
    public void onItemRemove(int position) {
        if(position >= 0 && position < list.size()) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(list, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public void onItemClone(int position) {
        DeviceEvent item = list.get(position).clone();
        list.add(position+1, item);
        notifyItemInserted(position+1);
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

    public void updateDeviceApi(DeviceAPI deviceAPI){
        commandAutoCompleteAdapter.update(deviceAPI);
    }
}

