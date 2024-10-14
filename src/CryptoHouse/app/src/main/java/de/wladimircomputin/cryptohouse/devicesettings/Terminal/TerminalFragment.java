package de.wladimircomputin.cryptohouse.devicesettings.Terminal;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.actions.config.CommandAutoCompleteAdapter;
import de.wladimircomputin.cryptohouse.assistant.FocusListener;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;
import de.wladimircomputin.cryptohouse.devicesettings.DeviceSettingsActivity;
import de.wladimircomputin.libcryptoiot.v2.protocol.Content;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConReceiver;
import de.wladimircomputin.libcryptoiot.v2.protocol.api.DeviceAPI;

public class TerminalFragment extends Fragment implements FocusListener {
    ScrollView terminalScroll;
    TextView terminalText;
    AutoCompleteTextView terminalPromptText;
    CommandAutoCompleteAdapter commandAutoCompleteAdapter;
    Button terminalLeft;
    Button terminalRight;
    Button terminalDown;
    Button terminalUp;
    Button terminalColon;

    List<String> history;
    int historyPos = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);
        terminalScroll = view.findViewById(R.id.terminalScroll);
        terminalText = view.findViewById(R.id.terminalText);
        terminalPromptText = view.findViewById(R.id.terminalPromptText);
        terminalLeft = view.findViewById(R.id.terminalLeft);
        terminalRight = view.findViewById(R.id.terminalRight);
        terminalDown = view.findViewById(R.id.terminalDown);
        terminalUp = view.findViewById(R.id.terminalUp);
        terminalColon = view.findViewById(R.id.terminalColon);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CryptCon cc = ((DeviceSettingsActivity)getActivity()).cc;
        DeviceManagerDevice device = ((DeviceSettingsActivity)getActivity()).device;
        commandAutoCompleteAdapter = new CommandAutoCompleteAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item);

        terminalText.setText(device.name + "-># ");

        terminalPromptText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                AutoCompleteTextView editText = ((AutoCompleteTextView) v);
                terminalText.append(editText.getText().toString());
                cc.sendMessageEncrypted(editText.getText().toString(), CryptCon.Mode.UDP, new CryptConReceiver() {
                    @Override
                    public void onSuccess(Content response) {
                        getActivity().runOnUiThread(() -> {
                            terminalText.append(editText.getText().toString() + "\n" + response.data);
                        });

                    }

                    @Override
                    public void onFail() {

                    }

                    @Override
                    public void onFinished() {
                        getActivity().runOnUiThread(() -> {
                            terminalText.append("\n\n" + device.name + "-># ");
                            terminalScroll.post(() -> {
                                terminalScroll.fullScroll(View.FOCUS_DOWN);
                            });
                        });
                    }

                    @Override
                    public void onProgress(String sprogress, int iprogress) {

                    }
                });
                history.remove(terminalPromptText.getText().toString());
                history.add(1, terminalPromptText.getText().toString());
                historyPos = 0;
                terminalPromptText.setText("");

                return true;
            }
            return false;
        });
        terminalPromptText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutoCompleteTextView v = terminalPromptText;
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
        terminalPromptText.setOnClickListener((v) -> {
            terminalPromptText.showDropDown();
        });
        terminalPromptText.setThreshold(0);
        terminalPromptText.setAdapter(commandAutoCompleteAdapter);

        terminalLeft.setOnClickListener(v -> {
            terminalPromptText.setSelection(terminalPromptText.getSelectionStart()-1);
        });

        terminalRight.setOnClickListener(v -> {
            terminalPromptText.setSelection(terminalPromptText.getSelectionStart()+1);
        });

        terminalDown.setOnClickListener(v -> {
            if(historyPos > 0) {
                terminalPromptText.setText(history.get(--historyPos));
            }
        });

        terminalUp.setOnClickListener(v -> {
            if(historyPos < history.size()-1) {
                terminalPromptText.setText(history.get(++historyPos));
            }
        });

        terminalColon.setOnClickListener(v -> {
            terminalPromptText.append(":");
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

    @Override
    public void onSelected() {
    }

    @Override
    public void onUnselected() {

    }
}