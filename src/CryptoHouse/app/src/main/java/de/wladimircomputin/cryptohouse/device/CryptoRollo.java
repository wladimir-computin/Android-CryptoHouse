package de.wladimircomputin.cryptohouse.device;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.SeekBar;

import org.json.JSONObject;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;
import de.wladimircomputin.libcryptoiot.v2.protocol.Content;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConReceiver;

public class CryptoRollo extends ACryptoDevice{

    SeekBar seekBar;

    public CryptoRollo(DeviceManagerDevice device, Context context) {
        super(device, context, R.layout.device_cryptorollo);

        seekBar = rootview.findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pos((double)seekBar.getProgress() / 100);
            }
        });
    }

    @Override
    public void update() {
        if(!seekBar.isPressed()) {
            cc.sendMessageEncrypted("Window:windowstate", CryptCon.Mode.UDP, new CryptConReceiver() {
                @Override
                public void onSuccess(Content response) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        titleText.setTextColor(context.getResources().getColor(R.color.colorAccent));
                        double progress = 0;
                        try {
                            progress = new JSONObject(response.data).getDouble("pos");
                        } catch (Exception x) {
                        }
                        if (!seekBar.isPressed()) {
                            seekBar.setProgress((int) (progress * 100), true);
                        }
                    });
                }

                @Override
                public void onFail() {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        titleText.setTextColor(context.getResources().getColor(R.color.colorRed));
                    });
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

    public void pos(double val){
        skipNextUpdate();
        cc.sendMessageEncrypted("Window:pos:" + val, CryptCon.Mode.UDP, new CryptConReceiver() {
            @Override
            public void onSuccess(Content response) {

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
}
