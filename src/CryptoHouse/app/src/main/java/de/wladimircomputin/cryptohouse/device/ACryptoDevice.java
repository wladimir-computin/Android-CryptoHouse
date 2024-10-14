package de.wladimircomputin.cryptohouse.device;

import static de.wladimircomputin.libcryptoiot.v2.Constants.command_applog;
import static de.wladimircomputin.libcryptoiot.v2.Constants.command_status;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;
import de.wladimircomputin.cryptohouse.devicesettings.DeviceSettingsActivity;
import de.wladimircomputin.libcryptoiot.v2.protocol.Content;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConReceiver;

public abstract class ACryptoDevice implements ICryptoDevice{
    protected DeviceManagerDevice device;
    protected View rootview;
    protected CryptCon cc;
    protected SharedPreferences sharedPref;
    protected Context context;

    protected Button statusButton;
    protected Button settingsButton;
    protected TextView titleText;
    protected ImageView arrowView;

    protected View genericDeviceView;

    protected boolean visible;
    boolean skipUpdate = false;

    public ACryptoDevice(DeviceManagerDevice device, Context context, int layoutfile){
        this.device = device;
        this.context = context;
        this.sharedPref = context.getSharedPreferences("de.wladimircomputin.cryptohouse.device." + device.id, Context.MODE_PRIVATE);
        this.cc = new CryptCon(device.pass, device.ip, device.key, device.key_probe);
        device.key = cc.getCrypter().getAesKey();
        device.key_probe = cc.getCrypter().getAesKeyProbe();
        rootview = LayoutInflater.from(context).inflate(layoutfile, null);
        titleText = rootview.findViewById(R.id.titleText);
        statusButton = rootview.findViewById(R.id.statusButton);
        settingsButton = rootview.findViewById(R.id.settingsButton);
        arrowView = rootview.findViewById(R.id.arrowView);
        genericDeviceView = rootview.findViewById(R.id.genericDeviceView);
        visible = sharedPref.getBoolean("visible", true);
        genericDeviceView.post(() -> {
            genericDeviceView.setTag(genericDeviceView.getHeight());
            if(!visible){
                genericDeviceView.getLayoutParams().height = 0;
                genericDeviceView.setLayoutParams(genericDeviceView.getLayoutParams());
                arrowView.setRotation(180f);
            }
        });

        titleText.setText(device.name);
        arrowView.setOnClickListener((view) -> {
            if(visible) {
                animateHeightTo(genericDeviceView, 0);
                arrowView.animate()
                        .rotation(180f)
                        .setDuration(300)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
                visible = false;
            } else {
                animateHeightTo(genericDeviceView, (int)genericDeviceView.getTag());
                arrowView.animate()
                        .rotation(0)
                        .setDuration(300)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
                visible = true;
            }
            sharedPref.edit().putBoolean("visible", visible).apply();
        });

        statusButton.setOnClickListener(v -> {
            cc.sendMessageEncrypted(command_status, CryptCon.Mode.UDP, new CryptConReceiver() {
                @Override
                public void onSuccess(Content response) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setMessage(response.data);
                        builder1.setCancelable(true);
                        builder1.setTitle(device.name + " " + context.getString(R.string.status));
                        builder1.show();
                    });
                }

                @Override
                public void onFail() {}

                @Override
                public void onFinished() {}

                @Override
                public void onProgress(String sprogress, int iprogress) {}
            });
        });

        statusButton.setOnLongClickListener(v -> {
            cc.sendMessageEncrypted(command_applog, CryptCon.Mode.UDP, new CryptConReceiver() {
                @Override
                public void onSuccess(Content response) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setMessage(response.data);
                        builder1.setCancelable(true);
                        builder1.setTitle(device.name + " " + context.getString(R.string.applog));
                        builder1.show();
                    });
                }

                @Override
                public void onFail() {}

                @Override
                public void onFinished() {}

                @Override
                public void onProgress(String sprogress, int iprogress) {}
            });
            return true;
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, DeviceSettingsActivity.class);
            intent.putExtra("id", device.id);
            intent.putExtra("name", device.name);
            intent.putExtra("ip", device.ip);
            intent.putExtra("pass", device.pass);
            intent.putExtra("type", device.type);
            context.startActivity(intent);
        });
    }

    protected void skipNextUpdate(){
        skipUpdate = true;
    }

    @Override
    public String getName() {
        return device.name;
    }

    @Override
    public View getRootView(){
        return rootview;
    }

    @Override
    public void updateMaySkip(){
        if(!skipUpdate) {
            update();
        } else {
            skipUpdate = false;
        }
    }

    @Override
    public abstract void update();

    @Override
    public CryptCon getCryptCon(){
        return cc;
    }

    @Override
    public DeviceManagerDevice getDeviceManagerItem(){
        return device;
    }

    @Override
    public void reloadSettings(){
        this.sharedPref = context.getSharedPreferences("de.wladimircomputin.cryptohouse.device." + device.id, Context.MODE_PRIVATE);
        this.cc = new CryptCon(device.pass, device.ip, device.key, device.key_probe);
        device.key = cc.getCrypter().getAesKey();
        device.key_probe = cc.getCrypter().getAesKeyProbe();
    }

    private void animateHeightTo(@NonNull View view, int height) {
        final int currentHeight = view.getHeight();
        ObjectAnimator animator = ObjectAnimator.ofInt(view, new HeightProperty(), currentHeight, height);
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

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }


}
