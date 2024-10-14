package de.wladimircomputin.cryptohouse.actions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.wladimircomputin.cryptohouse.MainActivity;
import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.actions.config.ActionConfigActivity;
import de.wladimircomputin.cryptohouse.actions.config.ActionDeviceItem;
import de.wladimircomputin.cryptohouse.actions.status.ActionItemRecyclerListAdapter;
import de.wladimircomputin.cryptohouse.actions.status.ActionStatusHolder;
import de.wladimircomputin.cryptohouse.boilerplate.SimpleItemTouchHelperCallback;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;
import de.wladimircomputin.libcryptoiot.v2.protocol.Content;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConBulkReceiver;

public class ActionFragment extends Fragment {

    private Toolbar toolbar;
    private ItemTouchHelper mItemTouchHelper;
    SharedPreferences sharedPrefs;
    ActionItemRecyclerListAdapter listAdapter;
    RecyclerView recyclerView;
    TextView actionsHint;
    String current_profile;
    HashMap<String, DeviceManagerDevice> deviceManagerItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_actions, container, false);
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.actions);

        current_profile = ((MainActivity)getActivity()).getCurrentProfile().id;
        sharedPrefs = getActivity().getSharedPreferences("de.wladimircomputin.cryptohouse.devices", Context.MODE_PRIVATE);
        String devicesjson = sharedPrefs.getString(current_profile, "{}");
        deviceManagerItems = new HashMap<>();
        try {
            JSONObject devicesObj = new JSONObject(devicesjson);
            JSONArray devicesArr = devicesObj.toJSONArray(devicesObj.names());
            if(devicesArr != null) {
                for (int i = 0; i < devicesArr.length(); i++) {
                    DeviceManagerDevice deviceManagerDevice = new DeviceManagerDevice(devicesArr.getJSONObject(i));
                    deviceManagerItems.put(deviceManagerDevice.id, deviceManagerDevice);
                }
            }
        } catch (JSONException x){
            x.printStackTrace();
        }

        listAdapter = new ActionItemRecyclerListAdapter(viewHolder -> mItemTouchHelper.startDrag(viewHolder), new ActionCallback() {
            @Override
            public void onActionClicked(ActionItem item) {
                for(ActionDeviceItem adi : item.action_device_items){
                    adi.cc.sendMessageEncryptedBulk(adi.commands, new CryptConBulkReceiver() {
                        @Override
                        public void onSuccess(Content response, int i) {

                        }

                        @Override
                        public void onFail(int i) {

                        }

                        @Override
                        public void onFinished() {

                        }

                        @Override
                        public void onProgress(String sprogress, int iprogress) {

                        }
                    });
                }
            }

            @Override
            public void onConfigClicked(ActionItem item) {
                Intent intent = new Intent(getActivity(), ActionConfigActivity.class);
                intent.putExtra("current_profile", current_profile);
                intent.putExtra("action_id", item.id);
                startActivity(intent);
            }

            @Override
            public void onActionStatusClicked(ActionDeviceItem item, ActionStatusHolder holder){
                item.cc.sendMessageEncryptedBulk(item.commands, new CryptConBulkReceiver() {
                    @Override
                    public void onSuccess(Content response, int i) {

                    }

                    @Override
                    public void onFail(int i) {

                    }

                    @Override
                    public void onFinished() {

                    }

                    @Override
                    public void onProgress(String sprogress, int iprogress) {

                    }
                });
            }
        }, deviceManagerItems, getContext());

        recyclerView = view.findViewById(R.id.actions_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        actionsHint = view.findViewById(R.id.actions_hint);
        actionsHint.setOnClickListener(view1 -> {
            add();
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(listAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        listAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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
                if(listAdapter.list.isEmpty() && actionsHint.getVisibility() != View.VISIBLE){
                    actionsHint.postDelayed(() -> {
                        actionsHint.setAlpha(0f);
                        actionsHint.setVisibility(View.VISIBLE);
                        actionsHint.animate().alpha(1f).setDuration(500);
                    }, 500);

                } else {
                    actionsHint.setVisibility(View.GONE);
                }
            }
        });

        loadActions();
        return view;
    }

    public void loadActions(){
        listAdapter.list.clear();
        recyclerView.removeAllViews();
        try {
            SharedPreferences sharedPrefs = getContext().getSharedPreferences("de.wladimircomputin.cryptohouse.actions", Context.MODE_PRIVATE);
            String actionsjson = sharedPrefs.getString(current_profile, "{}");
            try {
                JSONObject actions = new JSONObject(actionsjson);
                JSONArray actionsArr = actions.toJSONArray(actions.names());
                for(int i = 0; i < actions.length(); i++){
                    listAdapter.list.add(new ActionItem(actionsArr.getJSONObject(i), deviceManagerItems));
                }
            } catch (JSONException x){
                x.printStackTrace();
            }
        } catch (Exception x){}
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause(){
        super.onPause();
        apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadActions();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.options_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_apply:
                apply();
                break;

            case R.id.menu_add:
                add();
                break;

            default:
                break;
        }
        return true;
    }

    public void apply(){
        JSONObject jsonObject = new JSONObject();
        try {
            for(ActionItem actionItem : listAdapter.list){
                jsonObject.put(actionItem.id, actionItem.toJSON());
            }
            SharedPreferences sharedPrefs = getContext().getSharedPreferences("de.wladimircomputin.cryptohouse.actions", Context.MODE_PRIVATE);
            sharedPrefs.edit().putString(current_profile, jsonObject.toString()).apply();
        } catch (JSONException x){}
    }

    public void add(){
        apply();
        Intent intent = new Intent(getActivity(), ActionConfigActivity.class);
        intent.putExtra("current_profile", current_profile);
        intent.putExtra("action_id", "");
        startActivity(intent);
    }
}