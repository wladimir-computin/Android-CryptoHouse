package de.wladimircomputin.cryptohouse.devicecontrols;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.assistant.FocusListener;
import de.wladimircomputin.cryptohouse.device.ICryptoDevice;

public class DeviceControlsSubFragment extends Fragment implements FocusListener {

    Timer updateTimer;
    ArrayList<ICryptoDevice> devices;
    String name;

    public DeviceControlsSubFragment(String name, ArrayList<ICryptoDevice> devices){
        super();
        this.name = name;
        this.devices = devices;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_devicecontrolssub, container, false);
        LinearLayout mainView = view.findViewById(R.id.MainView);
        for (ICryptoDevice device : devices){
            ((ViewGroup) mainView).addView(device.getRootView());
        }

        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
        detachUpdateHandler();
    }

    @Override
    public void onResume() {
        super.onResume();
        attachUpdateHandler(0);
    }

    private void update(){
        for (ICryptoDevice device : devices){
            device.updateMaySkip();
        }
    }

    private void attachUpdateHandler(int after){
        detachUpdateHandler();
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, after, 1000);
    }

    private void detachUpdateHandler(){
        if(updateTimer != null) {
            updateTimer.cancel();
        }
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onUnselected() {

    }
}
