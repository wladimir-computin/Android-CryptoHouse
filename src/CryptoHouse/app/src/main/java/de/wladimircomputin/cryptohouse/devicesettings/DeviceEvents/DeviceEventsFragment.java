package de.wladimircomputin.cryptohouse.devicesettings.DeviceEvents;

import static de.wladimircomputin.libcryptoiot.v2.Constants.command_readSettings;
import static de.wladimircomputin.libcryptoiot.v2.Constants.command_reboot;
import static de.wladimircomputin.libcryptoiot.v2.Constants.command_writeSettings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.assistant.FocusListener;
import de.wladimircomputin.cryptohouse.boilerplate.SimpleItemTouchHelperCallback;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;
import de.wladimircomputin.cryptohouse.devicesettings.DeviceSettingsActivity;
import de.wladimircomputin.libcryptoiot.v2.protocol.Content;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConBulkReceiver;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConReceiver;
import de.wladimircomputin.libcryptoiot.v2.protocol.api.DeviceAPI;

public class DeviceEventsFragment extends Fragment implements FocusListener {

    SwitchCompat device_events_enabled_switch;
    TextView device_events_hint;
    RecyclerView device_events_recycler;
    DeviceEventRecyclerListAdapter deviceEventRecyclerListAdapter;
    private ItemTouchHelper mItemTouchHelper;

    List<DeviceEvent> deviceEvents = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_events, container, false);
        device_events_enabled_switch = view.findViewById(R.id.device_events_enabled_switch);
        device_events_hint = view.findViewById(R.id.device_events_hint);
        device_events_recycler = view.findViewById(R.id.device_events_recycler);
        setHasOptionsMenu(true);

        deviceEventRecyclerListAdapter = new DeviceEventRecyclerListAdapter(viewHolder -> mItemTouchHelper.startDrag(viewHolder), deviceEvents, new DeviceAPI(""), getContext());
        device_events_recycler.setHasFixedSize(true);
        device_events_recycler.setAdapter(deviceEventRecyclerListAdapter);
        device_events_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        device_events_hint.setOnClickListener(view1 -> {
            device_events_hint.setVisibility(View.GONE);
            add();
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(deviceEventRecyclerListAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(device_events_recycler);

        deviceEventRecyclerListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                onChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                onChanged();
            }

            public void onChanged() {
                super.onChanged();
                if (deviceEvents.isEmpty() && device_events_hint.getVisibility() != View.VISIBLE) {
                    device_events_hint.postDelayed(() -> {
                        if(deviceEvents.isEmpty()) {
                            device_events_hint.setAlpha(0f);
                            device_events_hint.setVisibility(View.VISIBLE);
                            device_events_hint.animate().alpha(1f).setDuration(500);
                        }
                    }, 500);
                } else if(!deviceEvents.isEmpty()) {
                    device_events_hint.post(() -> {
                        device_events_hint.setVisibility(View.GONE);
                    });

                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CryptCon cc = ((DeviceSettingsActivity) getActivity()).cc;
        DeviceManagerDevice device = ((DeviceSettingsActivity) getActivity()).device;
    }

    private void update() {
        CryptCon cc = ((DeviceSettingsActivity) getActivity()).cc;
        DeviceAPI deviceAPI = new DeviceAPI("");

        String[] commands = {command_readSettings + ":events:device_events_enabled", command_readSettings + ":events:device_events"};
        cc.sendMessageEncryptedBulk(commands, new CryptConBulkReceiver() {
            @Override
            public void onSuccess(Content response, int i) {
                if(i == 0){
                    getActivity().runOnUiThread(() -> {
                        device_events_enabled_switch.setChecked(response.data.equals("1"));
                    });
                } else if (i == 1){
                    try {
                        JSONArray jsonArray = new JSONArray(response.data);
                        int length = deviceEvents.size();
                        if(!deviceEvents.isEmpty()) {
                            deviceEvents.clear();
                            deviceEventRecyclerListAdapter.notifyItemRangeRemoved(0, length);
                        }
                        for (int x = 0; x < jsonArray.length(); x++) {
                            DeviceEvent deviceEvent = new DeviceEvent(jsonArray.optJSONObject(x));
                            deviceEvents.add(deviceEvent);
                        }
                        deviceEventRecyclerListAdapter.notifyItemRangeInserted(0, deviceEvents.size());
                    } catch (Exception x){

                    }
                }
            }

            @Override
            public void onFail(int i) {

            }

            @Override
            public void onFinished() {
                deviceAPI.generate(cc, new CryptConReceiver() {
                    @Override
                    public void onSuccess(Content response) {

                    }

                    @Override
                    public void onFail() {

                    }

                    @Override
                    public void onFinished() {
                        deviceEventRecyclerListAdapter.updateDeviceApi(deviceAPI);
                    }
                    @Override
                    public void onProgress(String sprogress, int iprogress) {

                    }
                });
            }

            @Override
            public void onProgress(String sprogress, int iprogress) {

            }
        });
    }

    private void add() {
        deviceEvents.add(new DeviceEvent("", ""));
        deviceEventRecyclerListAdapter.notifyItemInserted(deviceEvents.size()-1);
    }

    private void apply() {
        try {
            String command_enabled = command_writeSettings + ":events:device_events_enabled:" + (device_events_enabled_switch.isChecked() ? "1" : "0");
            String command_device_events = command_writeSettings + ":events:device_events:";
            JSONArray jsonArray = new JSONArray();
            for(DeviceEvent deviceEvent : deviceEvents){
                jsonArray.put(new JSONObject(deviceEvent.toJSON()));
            }
            command_device_events += jsonArray.toString();
            CryptCon cc = ((DeviceSettingsActivity) getActivity()).cc;
            cc.sendMessageEncryptedBulk(new String[]{command_enabled, command_device_events}, new CryptConBulkReceiver() {
                @Override
                public void onSuccess(Content response, int i) {

                }

                @Override
                public void onFail(int i) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(getContext(), "Write failed", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onFinished() {
                    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                cc.sendMessageEncrypted(command_reboot);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    };
                    getActivity().runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(getString(R.string.reboot_device))
                                .setMessage(getString(R.string.reboot_device_text))
                                .setPositiveButton(getString(R.string.yes), dialogClickListener)
                                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
                    });
                }

                @Override
                public void onProgress(String sprogress, int iprogress) {

                }
            });



        } catch (Exception x){

        }
    }

    @Override
    public void onResume(){
        super.onResume();
        update();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // device with ID device_refresh was selected

            case R.id.menu_apply:
                apply();
                return true;

            case R.id.menu_add:
                add();
                return true;

            default:
                break;
        }
        return false;
    }


    @Override
    public void onSelected() {
    }

    @Override
    public void onUnselected() {

    }
}
