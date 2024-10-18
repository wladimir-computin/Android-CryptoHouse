package de.wladimircomputin.cryptohouse.devicesettings.PersistentMemory;

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
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.assistant.FocusListener;
import de.wladimircomputin.cryptohouse.databinding.FragmentPersistentMemoryBinding;
import de.wladimircomputin.cryptohouse.devicesettings.DeviceSettingsActivity;
import de.wladimircomputin.cryptohouse.devicesettings.ExpandableListAdapter;
import de.wladimircomputin.libcryptoiot.v2.protocol.Content;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConBulkReceiver;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConReceiver;

public class PersistentMemoryFragment extends Fragment implements FocusListener {

    List<String> data = new ArrayList<>();
    LinkedHashMap<String, ArrayList<KVPair<String, String>>> dataDetail = new LinkedHashMap<>();

    FragmentPersistentMemoryBinding binding;
    ExpandableListAdapter expandableListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPersistentMemoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expandableListAdapter = new ExpandableListAdapter(getContext(), data, dataDetail);
        binding.expandableListView.setAdapter(expandableListAdapter);

        binding.expandableListView.setOnGroupExpandListener(groupPosition -> {
        });

        binding.expandableListView.setOnGroupCollapseListener(groupPosition -> {

        });

        binding.expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });

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
            // action with ID action_refresh was selected

            case R.id.menu_apply:
                apply();
                return true;

            default:
                break;
        }
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void update(){
        CryptCon cc =  ((DeviceSettingsActivity)getActivity()).cc;

        data.clear();
        dataDetail.clear();
        cc.sendMessageEncrypted(command_readSettings, CryptCon.Mode.UDP, new CryptConReceiver() {
            @Override
            public void onSuccess(Content response) {
                String[] vaults = response.data.split("\n");

                String[] reads = Arrays.stream(vaults).map(s -> command_readSettings + ":" + s).toArray(String[]::new);
                cc.sendMessageEncryptedBulk(reads, new CryptConBulkReceiver() {
                    @Override
                    public void onSuccess(Content response, int i) {
                        try {
                            ArrayList<KVPair<String, String>> settings = new ArrayList<>();
                            JSONObject jobj = new JSONObject(response.data);
                            for (int j = 0; j < jobj.length(); j++) {
                                String name = jobj.names().getString(j);
                                settings.add(new KVPair<>(name, jobj.getString(name)));
                            }
                            data.add(vaults[i]);
                            dataDetail.put(vaults[i], settings);
                            getActivity().runOnUiThread(() -> {
                                expandableListAdapter.notifyDataSetChanged();
                            });
                        } catch (Exception x){
                            x.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(int i) {

                    }

                    @Override
                    public void onFinished(){
                    }

                    @Override
                    public void onProgress(String sprogress, int iprogress) {

                    }
                });
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onProgress(String sprogress, int iprogress) {

            }
        });
    }

    public void apply(){
        CryptCon cc =  ((DeviceSettingsActivity)getActivity()).cc;

        List<String> commands = new ArrayList<>();
        for (String vault : dataDetail.keySet()){
            List<KVPair<String, String>> settings = dataDetail.get(vault);
            for (KVPair<String, String> setting : settings){
                if(setting.changed){
                    commands.add(command_writeSettings + ":" + vault + ":" + setting.key + ":" + setting.value);
                }
            }
        }

        if(!commands.isEmpty()) {
            cc.sendMessageEncryptedBulk(commands.toArray(new String[0]), new CryptConBulkReceiver() {
                @Override
                public void onSuccess(Content response, int i) {
                    commands.remove(0);
                }

                @Override
                public void onFail(int i) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(getContext(), "Command failed: " + commands.get(0), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onFinished() {
                    if (commands.isEmpty()) {
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
                }

                @Override
                public void onProgress(String sprogress, int iprogress) {

                }
            });
        }
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onUnselected() {

    }
}
