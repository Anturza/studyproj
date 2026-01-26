package mod1;

import java.util.Iterator;

/** An interface, defying some basic functionality for data structure stores "key-value" pairs. */
public interface CustomMap<K, V> {

    void put(K key, V value);

    V get(K key);

    V remove(K key);

    boolean containsKey(K key);

    boolean containsValue(V value);

    int size();

    int capacity();

    boolean isEmpty();

    void clear();

    Iterator<Entry<K, V>> iterator();

    interface Entry<K, V> {
        K getKey();

        V getValue();

        void setValue(V value);
    }

}
