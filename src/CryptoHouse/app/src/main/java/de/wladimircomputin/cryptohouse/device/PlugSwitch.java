package de.wladimircomputin.cryptohouse.device;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.widget.SwitchCompat;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;
import de.wladimircomputin.libcryptoiot.v2.protocol.Content;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConReceiver;

public class PlugSwitch extends ACryptoDevice{
    SwitchCompat sw;

    public PlugSwitch(DeviceManagerDevice device, Context context) {
        super(device, context, R.layout.device_plugswitch);
        sw = rootview.findViewById(R.id.switch1);

        sw.setOnClickListener((view) -> {
            setState(((SwitchCompat)view).isChecked());
        });
    }

    @Override
    public void update() {
        cc.sendMessageEncrypted("Switch:state", CryptCon.Mode.UDP, new CryptConReceiver() {
            @Override
            public void onSuccess(Content response) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    titleText.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    sw.setChecked(response.data.equals("1"));
                });
            }

            @Override
            public void onFail() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    titleText.setTextColor(context.getResources().getColor(R.color.colorRed));
                });
            }

            @Override
            public void onFinished() {}

            @Override
            public void onProgress(String sprogress, int iprogress) {}
        });
    }

    public void setState(boolean state){
        skipNextUpdate();
        cc.sendMessageEncrypted("Switch:switch:" + (state ? "1" : "0"), CryptCon.Mode.UDP, new CryptConReceiver() {
            @Override
            public void onSuccess(Content response) {}

            @Override
            public void onFail() {}

            @Override
            public void onFinished() {}

            @Override
            public void onProgress(String sprogress, int iprogress) {}
        });
    }
}
