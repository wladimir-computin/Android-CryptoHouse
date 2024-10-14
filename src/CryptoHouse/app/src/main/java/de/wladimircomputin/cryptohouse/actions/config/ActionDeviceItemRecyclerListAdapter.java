package de.wladimircomputin.cryptohouse.actions.config;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.boilerplate.ItemTouchHelperAdapter;
import de.wladimircomputin.cryptohouse.boilerplate.OnStartDragListener;
import de.wladimircomputin.cryptohouse.boilerplate.SimpleItemTouchHelperCallback;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;
import de.wladimircomputin.libcryptoiot.v2.protocol.Content;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConReceiver;
import de.wladimircomputin.libcryptoiot.v2.protocol.api.DeviceAPI;

public class ActionDeviceItemRecyclerListAdapter extends RecyclerView.Adapter<ActionDeviceItemHolder> implements ItemTouchHelperAdapter {

    public final List<ActionDeviceItem> list = new ArrayList<>();
    private final OnStartDragListener mDragStartListener;
    private ItemTouchHelper mItemTouchHelper;
    HashMap<String, DeviceManagerDevice> deviceManagerItems;

    Context context;

    public ActionDeviceItemRecyclerListAdapter(OnStartDragListener dragStartListener, HashMap<String, DeviceManagerDevice> deviceManagerItems, Context context) {
        mDragStartListener = dragStartListener;
        this.deviceManagerItems = deviceManagerItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ActionDeviceItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_device_item, parent, false);
        return new ActionDeviceItemHolder(view);
    }

    public void updateAutoCompleteAdapter(ActionDeviceItem item, CommandAutoCompleteAdapter commandAutoCompleteAdapter){
        DeviceAPI deviceAPI = new DeviceAPI(item.device_id);
        deviceAPI.generate(item.cc, new CryptConReceiver() {
            @Override
            public void onSuccess(Content response) {

            }

            @Override
            public void onFail() {

            }

            @Override
            public void onFinished() {
                if(!deviceAPI.isEmpty()) {
                    commandAutoCompleteAdapter.update(deviceAPI);
                }
            }

            @Override
            public void onProgress(String sprogress, int iprogress) {

            }
        });
    }

    @Override
    public void onBindViewHolder(final ActionDeviceItemHolder holder, int position) {
        final CommandAutoCompleteAdapter autoCompleteAdapter = new CommandAutoCompleteAdapter(context, R.layout.support_simple_spinner_dropdown_item);
        updateAutoCompleteAdapter( list.get(holder.getBindingAdapterPosition()), autoCompleteAdapter);
        ActionDeviceCommandRecyclerListAdapter actionDeviceCommandRecyclerListAdapter = new ActionDeviceCommandRecyclerListAdapter(viewHolder -> mItemTouchHelper.startDrag(viewHolder), new_commands -> {
        list.get(holder.getBindingAdapterPosition()).commands = new_commands;}, autoCompleteAdapter, context);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(actionDeviceCommandRecyclerListAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(holder.commandsRecycleview);
        holder.commandsRecycleview.setAdapter(actionDeviceCommandRecyclerListAdapter);
        holder.commandsRecycleview.setLayoutManager(new LinearLayoutManager(context));
        actionDeviceCommandRecyclerListAdapter.list.addAll(Arrays.asList(list.get(holder.getBindingAdapterPosition()).commands));
        actionDeviceCommandRecyclerListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                notifyItemChanged(-1);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                notifyItemChanged(-1);
            }
        });
        actionDeviceCommandRecyclerListAdapter.notifyDataSetChanged();

        holder.addButton.setOnClickListener(v -> {
            actionDeviceCommandRecyclerListAdapter.list.add("");
            actionDeviceCommandRecyclerListAdapter.notifyItemInserted(actionDeviceCommandRecyclerListAdapter.getItemCount());
        });

        holder.deleteButton.setOnClickListener(v -> {
            onItemRemove(holder.getBindingAdapterPosition());
        });
        DeviceManagerDevice[] arr = deviceManagerItems.values().toArray(new DeviceManagerDevice[0]);
        Arrays.sort(arr, Comparator.comparing(t0 -> t0.name));
        ArrayAdapter<DeviceManagerDevice> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, arr);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.deviceSpinner.setAdapter(spinnerArrayAdapter);
        String id = list.get(holder.getBindingAdapterPosition()).device_id;
        if (deviceManagerItems.containsKey(id)){
            holder.deviceSpinner.setOnItemSelectedListener(null);
            DeviceManagerDevice selection = deviceManagerItems.get(id);
            holder.deviceSpinner.setSelection(spinnerArrayAdapter.getPosition(selection));
        }

        holder.deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DeviceManagerDevice deviceManagerDevice = (DeviceManagerDevice) holder.deviceSpinner.getSelectedItem();
                ActionDeviceItem actionDeviceItem = list.get(holder.getBindingAdapterPosition());
                if(!actionDeviceItem.device_id.equals(deviceManagerDevice.id)) {
                    actionDeviceItem.device_id = deviceManagerDevice.id;
                    actionDeviceItem.cc = new CryptCon(deviceManagerDevice.pass, deviceManagerDevice.ip, deviceManagerDevice.key, deviceManagerDevice.key_probe);
                    updateAutoCompleteAdapter(list.get(holder.getBindingAdapterPosition()), actionDeviceCommandRecyclerListAdapter.autoCompleteAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

