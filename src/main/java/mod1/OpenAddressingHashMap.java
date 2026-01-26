package mod1;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * This custom realization of HashMap (only in study purposes) has a base functionality to store data in key-value
 * pairs, providing efficient retrieval, insertion, and deletion operations. On best case, core operations like put(),
 * get(), and remove() run in constant time (O(1)) due to the use of hashing. In cases of hash collisions, it uses
 * Open Addressing (linear probing) method to find an empty cell (bucket). This HashMap is not thread-safe. It can store
 * one null key and multiple null values
 */

public class OpenAddressingHashMap<K, V> implements CustomMap<K, V> {

    private final float INITIAL_LOAD_FACTOR = 0.75f;
    private final int INITIAL_CAPACITY = 16;

    //Entity, to store pair "key-value"
    static class Cell<K, V> implements CustomMap.Entry<K, V> {
        private K key;
        private V value;

        Cell(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public void setValue(V value) {
            this.value = value;
        }
    }

    //Main arrays of cells (buckets)
    private Cell<K, V>[] cellArray;
    private int size;
    private int capacity;
    private int loadFactor;

    // Marker for deleted entries in open addressing (it could break the probe sequences of other elements that hashed
    // to the same initial location
    private final Cell<K, V> REMOVED = new Cell<>(null, null);

    //Allowing to store null key separately
    private boolean hasANullKey = false;
    private Cell<K, V> nullKeyValue = new Cell<>(null, null);

    @SuppressWarnings("unchecked")
    public OpenAddressingHashMap() {
        capacity = INITIAL_CAPACITY;
        cellArray = new Cell[capacity]; //Using SuppressWarnings because of assurance with types
        size = 0;
        loadFactor = (int) (capacity * INITIAL_LOAD_FACTOR);
    }

    private int hash(K key) {
        // Use Java's hashCode(), ensure non-negative index
        return key == null ? 0 : Math.abs(key.hashCode() % capacity);
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            putNullKey(value);
            return;
        }

        if (size >= loadFactor) {
            resize();
        }

        int index = hash(key);

        while (cellArray[index] != null && cellArray[index] != REMOVED && !cellArray[index].key.equals(key)) {
            index = (index + 1) % capacity; // Linear probing: move to the next slot
        }

        if (cellArray[index] == null || cellArray[index] == REMOVED) {
            size++;
        }
        cellArray[index] = new Cell<>(key, value);
    }

    private void putNullKey(V value) {
        if (!hasANullKey) {
            size++;
        }
        hasANullKey = true;
        nullKeyValue.setValue(value);
    }

    @Override
    public V get(K key) {
        if (key == null) return hasANullKey ? nullKeyValue.getValue() : null;

        int hash = hash(key);
        int index = hash;

        while (cellArray[index] != null) {
            if (cellArray[index] != REMOVED && cellArray[index].getKey().equals(key)) {
                return cellArray[index].getValue();
            }
            index = (index + 1) % capacity; // Continue probing
            if (index == hash) break; // Avoid infinite loop if cellsArray is full or search wraps around
        }
        return null;
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            if (hasANullKey) {
                return removeNullKey();
            } else return null;
        }

        int hash = hash(key);
        int index = hash;

        while (cellArray[index] != null) {
            if (cellArray[index] != REMOVED && cellArray[index].getKey().equals(key)) {
                V oldValue = cellArray[index].getValue();
                cellArray[index] = REMOVED; // Mark as deleted, don't just set to null (or it will break probing)
                size--;
                return oldValue;
            }
            index = (index + 1) % capacity;
            if (index == hash) break;
        }
        return null;
    }

    private V removeNullKey() {
        V oldNullValue = nullKeyValue.getValue();
        hasANullKey = false;
        nullKeyValue = null;
        size--;
        return oldNullValue;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        capacity *= 2;
        Cell<K, V>[] oldCellArray = cellArray;
        cellArray = new Cell[capacity];
        size = hasANullKey ? 1 : 0;
        loadFactor = (int) (capacity * INITIAL_LOAD_FACTOR);

        for (Cell<K, V> cell : oldCellArray) {
            if (cell != null && cell != REMOVED) {
                put(cell.getKey(), cell.getValue());
            }
        }
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            return hasANullKey;
        }
        int index = hash(key);
        while (cellArray[index] != null) {
            if (cellArray[index].getKey().equals(key)) {
                return true;
            }
            index = (index + 1) % cellArray.length;
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        if (hasANullKey && Objects.equals(value, nullKeyValue.getValue())) {
            return true;
        }
        for (CustomMap.Entry<K, V> cell : cellArray) {
            if (cell != null && cell != REMOVED) {
                // Handle potential null values during comparison
                if ((cell.getValue() == value) || (value != null && value.equals(cell.getValue()))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public void clear() {
        CustomMap.Entry<K, V>[] newCellsArray;
        if ((newCellsArray = cellArray) != null && size > 0) {
            size = 0;
            Arrays.fill(newCellsArray, null);
        }
    }

    public Iterator<Entry<K, V>> iterator() {
        return new CustomIterator();
    }

    private class CustomIterator implements Iterator<Entry<K, V>> {
        private int currentCellIndex;
        private Cell<K, V> currentCell;
        private boolean gotNull;

        public CustomIterator() {
            currentCellIndex = 0;
            currentCell = hasANullKey ? nullKeyValue : null;
            gotNull = false;
        }

        @Override
        public boolean hasNext() {
            // Move to the next non-empty bucket
            for (int i = currentCellIndex; i < capacity; i++) {
                if (cellArray[i] != null) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Cell<K, V> next() {
            if (hasANullKey && !gotNull) {
                Cell<K, V> cellForNull = currentCell;
                currentCell = null;
                gotNull = true;
                return cellForNull;
            }
            while (currentCellIndex < capacity) {
                if (cellArray[currentCellIndex] != null && cellArray[currentCellIndex] != REMOVED) {
                    currentCell = cellArray[currentCellIndex];
                    currentCellIndex++;
                    return currentCell;
                }
                currentCellIndex++;
            }
            throw new NoSuchElementException("No more elements in the map.");
        }
    }
}
