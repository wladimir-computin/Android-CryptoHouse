package de.wladimircomputin.libcryptoiot;

import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConReceiver;

public class CryptoIoT {
    enum ProtocolVersion {
        AUTO,
        V1,
        V2
    }

    private de.wladimircomputin.libcryptoiot.v1.protocol.CryptCon cc1;
    private de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon cc2;

    private ProtocolVersion protocol_version = ProtocolVersion.AUTO;

    public CryptoIoT(String pass, String host) {
        cc1 = new de.wladimircomputin.libcryptoiot.v1.protocol.CryptCon(pass, host);
        cc2 = new de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon(pass, host);
    }

    public CryptoIoT(String pass, String host, String key, String key_probe) {
        cc1 = new de.wladimircomputin.libcryptoiot.v1.protocol.CryptCon(pass, host, key, key_probe);
        cc2 = new de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon(pass, host, key, key_probe);
    }

    public static void discoverDevices_v1(de.wladimircomputin.libcryptoiot.v1.protocol.CryptConDiscoverReceiver receiver) {
        de.wladimircomputin.libcryptoiot.v1.protocol.CryptCon.discoverDevices(receiver);
    }

    public static void discoverDevices_v2(de.wladimircomputin.libcryptoiot.v2.protocol.CryptConDiscoverReceiver receiver) {
        de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon.discoverDevices(receiver);
    }
}
