package de.wladimircomputin.cryptohouse.actions.config;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.actions.ActionItem;
import de.wladimircomputin.cryptohouse.boilerplate.SimpleItemTouchHelperCallback;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;

public class ActionConfigActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText nameEdittext;
    private RecyclerView actionDeviceRecycleview;
    private TextView actionsDevicesHint;
    String action_id;
    String current_profile;
    ActionItem actionItem;
    SharedPreferences sharedPrefs;
    HashMap<String, DeviceManagerDevice> deviceManagerItems;
    ActionDeviceItemRecyclerListAdapter actionDeviceItemRecyclerListAdapter;
    ItemTouchHelper mItemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_config);
        nameEdittext = findViewById(R.id.action_name_edittext);
        actionDeviceRecycleview = findViewById(R.id.action_config_recycleview);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionsDevicesHint = findViewById(R.id.actions_devices_hint);
        actionsDevicesHint.setOnClickListener(view1 -> {
            add();
        });

        Intent intent = getIntent();
        current_profile = intent.getStringExtra("current_profile");
        action_id = intent.getStringExtra("action_id");
        deviceManagerItems = new HashMap<>();

        sharedPrefs = getSharedPreferences("de.wladimircomputin.cryptohouse.devices", Context.MODE_PRIVATE);
        String devicesjson = sharedPrefs.getString(current_profile, "{}");
        try {
            JSONObject devicesObj = new JSONObject(devicesjson);
            JSONArray devicesArr = devicesObj.toJSONArray(devicesObj.names());
            if (devicesArr != null) {
                for (int i = 0; i < devicesArr.length(); i++) {
                    DeviceManagerDevice deviceManagerDevice = new DeviceManagerDevice(devicesArr.getJSONObject(i));
                    if(!deviceManagerDevice.type.equals("divider")) {
                        deviceManagerItems.put(deviceManagerDevice.id, deviceManagerDevice);
                    }
                }

                actionDeviceItemRecyclerListAdapter = new ActionDeviceItemRecyclerListAdapter(viewHolder -> mItemTouchHelper.startDrag(viewHolder), deviceManagerItems, this);
                ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(actionDeviceItemRecyclerListAdapter);
                mItemTouchHelper = new ItemTouchHelper(callback);
                mItemTouchHelper.attachToRecyclerView(actionDeviceRecycleview);
                actionDeviceRecycleview.setAdapter(actionDeviceItemRecyclerListAdapter);
                actionDeviceRecycleview.setLayoutManager(new LinearLayoutManager(this));
                actionDeviceItemRecyclerListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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

                    @Override
                    public void onChanged() {
                        super.onChanged();
                        if(actionDeviceItemRecyclerListAdapter.list.isEmpty() && actionsDevicesHint.getVisibility() != View.VISIBLE){
                            actionsDevicesHint.postDelayed(() -> {
                                actionsDevicesHint.setAlpha(0f);
                                actionsDevicesHint.setVisibility(View.VISIBLE);
                                actionsDevicesHint.animate().alpha(1f).setDuration(500);
                            }, 500);
                        } else {
                            actionsDevicesHint.setVisibility(View.GONE);
                        }
                    }
                });
            }
            sharedPrefs = getSharedPreferences("de.wladimircomputin.cryptohouse.actions", MODE_PRIVATE);
            if (!action_id.isEmpty()) {
                String actionsjson = sharedPrefs.getString(current_profile, "{}");
                try {
                    JSONObject actions = new JSONObject(actionsjson);
                    actionItem = new ActionItem(actions.getJSONObject(action_id), deviceManagerItems);
                } catch (JSONException x) {
                    actionItem = new ActionItem(UUID.randomUUID().toString(), nameEdittext.getText().toString(), new ActionDeviceItem[]{});
                    x.printStackTrace();
                }
                actionDeviceItemRecyclerListAdapter.list.addAll(Arrays.asList(actionItem.action_device_items));
            } else {
                actionItem = new ActionItem(UUID.randomUUID().toString(), nameEdittext.getText().toString(), new ActionDeviceItem[]{});
            }
            actionDeviceItemRecyclerListAdapter.notifyDataSetChanged();

            nameEdittext.setText(actionItem.name);
            getSupportActionBar().setTitle(actionItem.name);


        } catch (JSONException x){
            x.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_apply:
                apply();
                break;

            case R.id.menu_add:
                add();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void add() {
        int position;
        LinearLayoutManager layoutManager = ((LinearLayoutManager) actionDeviceRecycleview.getLayoutManager());
        if(!actionDeviceItemRecyclerListAdapter.list.isEmpty()) {
            if(!actionDeviceRecycleview.canScrollVertically(1)){
                position = layoutManager.getItemCount();
            } else {
                position = layoutManager.findFirstCompletelyVisibleItemPosition();
            }
        } else {
            position = 0;
        }
        actionDeviceItemRecyclerListAdapter.list.add(position, new ActionDeviceItem("0", null, new String[]{""}));
        actionDeviceItemRecyclerListAdapter.notifyItemInserted(position);
        if(position==0){
            new Handler().postDelayed(() -> {
                actionDeviceRecycleview.smoothScrollToPosition(0);
            }, 500);
        } else if (position == layoutManager.getItemCount() - 1){
            new Handler().postDelayed(() -> {
                actionDeviceRecycleview.smoothScrollToPosition(actionDeviceItemRecyclerListAdapter.list.size() - 1);
            }, 500);
        }
    }

    private void apply() {
        actionItem.name = nameEdittext.getText().toString();
        actionItem.action_device_items = actionDeviceItemRecyclerListAdapter.list.toArray(new ActionDeviceItem[0]);
        String actionsjson = sharedPrefs.getString(current_profile, "{}");
        try {
            JSONObject actions = new JSONObject(actionsjson);
            actions.put(actionItem.id, actionItem.toJSON());
            sharedPrefs.edit().putString(current_profile, actions.toString()).apply();
            finish();
        } catch (JSONException x){
            x.printStackTrace();
        }
    }
}
