package de.wladimircomputin.libcryptoiot;

public interface ICrypter {
    public byte[] encrypt(byte[] message, byte[] iv);
    public byte[] decrypt(byte[] message, byte[] iv, byte[] tag);
    public byte[] getRandom(int len);
    public String getAesKey();
    public String getAesKeyProbe();
}
