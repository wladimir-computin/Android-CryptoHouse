package de.wladimircomputin.cryptohouse.device;

import android.content.Context;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;

public class CryptoDimmer2 extends ACryptoDimmer {
    public CryptoDimmer2(DeviceManagerDevice device, Context context) {
        super(device, context, R.layout.device_cryptodimmer2);
    }
}