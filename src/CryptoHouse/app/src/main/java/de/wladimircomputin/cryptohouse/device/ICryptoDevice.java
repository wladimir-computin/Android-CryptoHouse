package de.wladimircomputin.cryptohouse.device;

import android.view.View;

import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;

public interface ICryptoDevice {
    String getName();
    View getRootView();
    void update();
    void updateMaySkip();
    CryptCon getCryptCon();
    DeviceManagerDevice getDeviceManagerItem();
    void reloadSettings();
}
