package de.wladimircomputin.cryptohouse.devicemanager;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConDiscoverReceiver;
import de.wladimircomputin.libcryptoiot.v2.protocol.DiscoveryDevice;

public class DeviceRecyclerListAdapter extends RecyclerView.Adapter<DeviceManagerHeaderHolder> implements ItemTouchHelperAdapter {

    public final List<DeviceManagerDevice> list = new ArrayList<>();
    private final OnStartDragListener mDragStartListener;
    private final Context context;

    public DeviceRecyclerListAdapter(Context context, OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).type.equals("divider")) {
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public DeviceManagerHeaderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.devicemanager_divider, parent, false);
            return new DeviceManagerDividerHolder(view, parent.getContext());
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.devicemanager_device, parent, false);
            return new DeviceManagerDeviceHolder(view, parent.getContext());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceManagerHeaderHolder holder, int position) {
        if (holder instanceof DeviceManagerDeviceHolder) {
            ((DeviceManagerDeviceHolder)holder).nameEdittext.setText(list.get(position).name);

            ((DeviceManagerDeviceHolder)holder).deviceSettingsView.post(() -> {
                if(((DeviceManagerDeviceHolder)holder).deviceSettingsView.getHeight() != 0){
                    ((DeviceManagerDeviceHolder)holder).deviceSettingsView.setTag(((DeviceManagerDeviceHolder)holder).deviceSettingsView.getHeight());
                }
                if(!list.get(position).settingsVisible){
                    ((DeviceManagerDeviceHolder)holder).deviceSettingsView.getLayoutParams().height = 0;
                    ((DeviceManagerDeviceHolder)holder).deviceSettingsView.setLayoutParams(((DeviceManagerDeviceHolder)holder).deviceSettingsView.getLayoutParams());
                    ((DeviceManagerDeviceHolder)holder).arrowView.setRotation(180f);
                } else {
                    ((DeviceManagerDeviceHolder)holder).deviceSettingsView.getLayoutParams().height = (int) ((DeviceManagerDeviceHolder)holder).deviceSettingsView.getTag();
                    ((DeviceManagerDeviceHolder)holder).deviceSettingsView.setLayoutParams(((DeviceManagerDeviceHolder)holder).deviceSettingsView.getLayoutParams());
                    ((DeviceManagerDeviceHolder)holder).arrowView.setRotation(0);
                }
            });

            ((DeviceManagerDeviceHolder)holder).arrowView.setOnClickListener((view) -> {
                if(list.get(position).settingsVisible) {
                    animateHeightTo( ((DeviceManagerDeviceHolder)holder).deviceSettingsView, 0);
                    ((DeviceManagerDeviceHolder) holder).arrowView.animate()
                            .rotation(180f)
                            .setDuration(300)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                    list.get(position).settingsVisible = false;
                } else {
                    animateHeightTo( ((DeviceManagerDeviceHolder)holder).deviceSettingsView, (int) ((DeviceManagerDeviceHolder)holder).deviceSettingsView.getTag());
                    ((DeviceManagerDeviceHolder)holder).arrowView.animate()
                            .rotation(0)
                            .setDuration(300)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                    list.get(position).settingsVisible = true;
                }
            });
            ((DeviceManagerDeviceHolder)holder).hostnameText.setText(list.get(position).hostname);
            ((DeviceManagerDeviceHolder)holder).macText.setText(list.get(position).mac);
            ((DeviceManagerDeviceHolder)holder).ipEdittext.setText(list.get(position).ip);
            ((DeviceManagerDeviceHolder)holder).devpassEdittext.setText(list.get(position).pass);
            ((DeviceManagerDeviceHolder)holder).autoIPCheckbox.setChecked((list.get(position)).update_ip);
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) ((DeviceManagerDeviceHolder) holder).devicetypeSpinner.getAdapter();
            int pos = adapter.getPosition(list.get(position).type);
            if (pos == -1) {
                pos = adapter.getPosition(context.getResources().getStringArray(R.array.devicetypes)[0]);
            }
            ((DeviceManagerDeviceHolder) holder).devicetypeSpinner.setSelection(pos);

            ((DeviceManagerDeviceHolder) holder).deviceItemScanButton.setOnClickListener(v -> {
                onItemScanIP(holder.getBindingAdapterPosition());
            });

            ((DeviceManagerDeviceHolder) holder).nameEdittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    list.get(holder.getBindingAdapterPosition()).name = s.toString();
                }
            });

            ((DeviceManagerDeviceHolder) holder).ipEdittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ((DeviceManagerDevice)list.get(holder.getBindingAdapterPosition())).ip = s.toString();
                }
            });

            ((DeviceManagerDeviceHolder) holder).devpassEdittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ((DeviceManagerDevice)list.get(holder.getBindingAdapterPosition())).pass = s.toString();
                }
            });

            ((DeviceManagerDeviceHolder) holder).devicetypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    list.get(holder.getBindingAdapterPosition()).type = adapter.getItem(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            ((DeviceManagerDeviceHolder) holder).autoIPCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                list.get(holder.getBindingAdapterPosition()).update_ip = isChecked;
            });

        } else if (holder instanceof DeviceManagerDividerHolder){
            ((DeviceManagerDividerHolder)holder).nameEdittext.setText(list.get(position).name);
            ((DeviceManagerDividerHolder) holder).nameEdittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    list.get(holder.getBindingAdapterPosition()).name = s.toString();
                }
            });
        }

        //holder.nameEdittext.setText(list.get(position).name);

        holder.drag.setOnLongClickListener(v -> {
            mDragStartListener.onStartDrag(holder);
            return true;
        });

        holder.cloneButton.setOnClickListener(v -> {
            onItemClone(holder.getBindingAdapterPosition());
        });

        holder.deleteButton.setOnClickListener(v -> {
            onItemRemove(holder.getBindingAdapterPosition());
        });

        try {




        } catch (Exception x){}

    }

    @Override
    public void onItemRemove(int position) {
        if (position >= 0 && position < list.size()) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void onItemClone(int position) {
        DeviceManagerDevice item = list.get(position).clone();
        item.id = UUID.randomUUID().toString();
        list.add(position + 1, item);
        notifyItemInserted(position + 1);
    }

    public void onItemScanIP(int position) {
        DeviceManagerDevice item = list.get(position);
        DeviceManagerDevice backup = item.clone();
        CryptCon.discoverDevices(new CryptConDiscoverReceiver() {
            @Override
            public void onSuccess(List<DiscoveryDevice> results) {
                for (DiscoveryDevice d : results) {
                    if (item.equalsToDiscoveryDevice(d)) {
                        item.hostname = d.name;
                        item.ip = d.ip;
                        item.mac = d.mac;
                        item.type = d.type;
                        if (!backup.equals(item)) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                notifyItemChanged(position);
                                Toast.makeText(context, context.getString(R.string.found_new_host) + ": " + item.ip, Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                Toast.makeText(context, context.getString(R.string.no_change_needed), Toast.LENGTH_SHORT).show();
                            });
                        }
                        return;
                    }
                }
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, context.getString(R.string.no_mathing_device), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFail() {
            }

            @Override
            public void onFinished() {
            }
        });
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
        ObjectAnimator animator = ObjectAnimator.ofInt(view, new DeviceRecyclerListAdapter.HeightProperty(), currentHeight, height);
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