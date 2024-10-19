package de.wladimircomputin.cryptohouse.devicesettings.Terminal;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.actions.config.CommandAutoCompleteAdapter;
import de.wladimircomputin.cryptohouse.assistant.FocusListener;
import de.wladimircomputin.cryptohouse.databinding.FragmentTerminalBinding;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;
import de.wladimircomputin.cryptohouse.devicesettings.DeviceSettingsActivity;
import de.wladimircomputin.libcryptoiot.v2.protocol.Content;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConReceiver;
import de.wladimircomputin.libcryptoiot.v2.protocol.api.DeviceAPI;

public class TerminalFragment extends Fragment implements FocusListener {
    CommandAutoCompleteAdapter commandAutoCompleteAdapter;

    FragmentTerminalBinding binding;

    List<String> history;
    int historyPos = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentTerminalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CryptCon cc = ((DeviceSettingsActivity)getActivity()).cc;
        DeviceManagerDevice device = ((DeviceSettingsActivity)getActivity()).device;
        commandAutoCompleteAdapter = new CommandAutoCompleteAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item);

        binding.terminalText.setText(device.name + "-># ");

        binding.terminalPromptText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                AutoCompleteTextView editText = ((AutoCompleteTextView) v);
                binding.terminalText.append(editText.getText().toString());
                cc.sendMessageEncrypted(editText.getText().toString(), CryptCon.Mode.UDP, new CryptConReceiver() {
                    @Override
                    public void onSuccess(Content response) {
                        getActivity().runOnUiThread(() -> {
                            binding.terminalText.append(editText.getText().toString() + "\n" + response.data);
                        });

                    }

                    @Override
                    public void onFail() {

                    }

                    @Override
                    public void onFinished() {
                        getActivity().runOnUiThread(() -> {
                            binding.terminalText.append("\n\n" + device.name + "-># ");
                            binding.terminalScroll.post(() -> {
                                binding.terminalScroll.fullScroll(View.FOCUS_DOWN);
                            });
                        });
                    }

                    @Override
                    public void onProgress(String sprogress, int iprogress) {

                    }
                });
                history.remove(binding.terminalPromptText.getText().toString());
                history.add(1, binding.terminalPromptText.getText().toString());
                historyPos = 0;
                binding.terminalPromptText.setText("");

                return true;
            }
            return false;
        });
        binding.terminalPromptText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutoCompleteTextView v = binding.terminalPromptText;
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
        binding.terminalPromptText.setOnClickListener((v) -> {
            binding.terminalPromptText.showDropDown();
        });
        binding.terminalPromptText.setThreshold(0);
        binding.terminalPromptText.setAdapter(commandAutoCompleteAdapter);

        binding.terminalLeft.setOnClickListener(v -> {
            moveCursor(-1);
        });

        binding.terminalRight.setOnClickListener(v -> {
            moveCursor(+1);
        });

        binding.terminalDown.setOnClickListener(v -> {
            if(historyPos > 0) {
                binding.terminalPromptText.setText(history.get(--historyPos));
            }
        });

        binding.terminalUp.setOnClickListener(v -> {
            if(historyPos < history.size()-1) {
                binding.terminalPromptText.setText(history.get(++historyPos));
            }
        });

        binding.terminalColon.setOnClickListener(v -> {
            binding.terminalPromptText.append(":");
        });
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        history = new ArrayList<>();
        history.add("");
    }

    @Override
    public void onResume(){
        super.onResume();
        updateAutoCompleteAdapter(((DeviceSettingsActivity)getActivity()).cc, commandAutoCompleteAdapter);
    }


    public void updateAutoCompleteAdapter(CryptCon cc, CommandAutoCompleteAdapter commandAutoCompleteAdapter){
        DeviceAPI deviceAPI = new DeviceAPI("");
        deviceAPI.generate(cc, new CryptConReceiver() {
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

    public void moveCursor(int where){
        int pos = Math.min(binding.terminalPromptText.getText().length(), Math.max(0, binding.terminalPromptText.getSelectionStart()+where));
        binding.terminalPromptText.setSelection(pos);
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onUnselected() {

    }
}