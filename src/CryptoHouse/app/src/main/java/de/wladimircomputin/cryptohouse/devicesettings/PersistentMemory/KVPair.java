package de.wladimircomputin.cryptohouse.devicesettings.PersistentMemory;

import java.util.Objects;

public class KVPair<K, V> {
    public K key;
    public V value;
    public boolean changed;
    public KVPair(K key, V value){
        this.key = key;
        this.value = value;
        this.changed = false;
    }
    public KVPair(KVPair<K,V> kvPair){
        this(kvPair.key, kvPair.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KVPair<?, ?> kvPair = (KVPair<?, ?>) o;
        return changed == kvPair.changed && Objects.equals(key, kvPair.key) && Objects.equals(value, kvPair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, changed);
    }
}
