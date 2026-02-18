package mod1;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**This custom realization of HashMap (only in study purposes) has the base functionality to store data in key-value
 * pairs, providing efficient retrieval, insertion, and deletion operations. On best case, core operations like put(),
 * get(), and remove() run in constant time (O(1)) due to the use of hashing. In cases of hash collisions, it uses
 * Separate Chaining to insert data in the same bucket. The order of elements in a HashMap is not guaranteed and can
 * change over time. This HashMap is not thread-safe. It can store one null key and multiple null values*/

public class ChainedHashMap<K, V> implements CustomMap<K, V>{

    private final int INITIAL_SIZE = 0;
    private final int INITIAL_CAPACITY = 16;
    private final float INITIAL_LOADFACTOR = 0.75f;

    //Entity, which stores the pair "key-value" of objects
    static class Cell<K, V> implements CustomMap.Entry<K, V>{
        final int hash;
        private K key;
        private V value;
        private Cell<K, V> next;

        public Cell(K key, V value, int hash) {
            this.key = key;
            this.value = value;
            this.hash = hash;
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

    //Main array of Cells (buckets)
    private Cell<K, V>[] cellArray;

    private int size;
    private int capacity;
    private double loadFactor;

    @SuppressWarnings("unchecked")
    public ChainedHashMap() {
        capacity = INITIAL_CAPACITY;
        cellArray = new Cell[capacity]; //Assure with types
        size = INITIAL_SIZE;
        loadFactor = (int) (capacity * INITIAL_LOADFACTOR);
    }

    // Use Java's hashCode(), ensure non-negative index
    private int indexFromHash(K key) {
        return key == null ? 0 : Math.abs(key.hashCode() % capacity);
    }

    @Override
    public void put (K key, V value) {
        if (size >= loadFactor) {
            resize();
        }
        //Get key hash
        int hash = key != null ? Math.abs(key.hashCode()) : 0;
        //Get bucket index
        int index = indexFromHash(key);
        Cell<K, V> newCell = new Cell<>(key, value, hash);

        if (cellArray[index] == null) {
            cellArray[index] = newCell;
        } else {
            Cell<K, V> currentCell = cellArray[index];
            while(true) {
                //Check by hash and then by equals
                if (currentCell.hash == hash && Objects.equals(currentCell.getKey(), key)) {
                    currentCell.setValue(value);
                    return;
                }
                //Add new cell in chain tail
                if (currentCell.next == null) {
                    currentCell.next = newCell;
                    break;
                }
                //Traverse on chain
                currentCell = currentCell.next;
            }
        }
        size++;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        capacity *= 2; //Doubling capacity
        Cell<K, V>[] oldCellArray = cellArray;
        cellArray = new Cell[capacity];
        size = 0;
        loadFactor = (int) (capacity * INITIAL_LOADFACTOR);
        // Iterate on main cells array
        for (Cell<K, V> cell : oldCellArray) {
            if (cell != null) {
                Cell<K, V> current = cell;
                //Traverse on chain of cells
                while (current != null) {
                    //put elements in new map will rehash keys
                    put(current.getKey(), current.getValue());
                    current = current.next;
                }
            }
        }
    }

    @Override
    public V get(K key) {
        int hash = key != null ? key.hashCode() : 0;
        int index = indexFromHash(key);
        Cell<K, V> currentCell = cellArray[index];

        while (currentCell != null) {
            if (currentCell.hash == hash && Objects.equals(currentCell.getKey(), key)) {
                return currentCell.getValue();
            }
            currentCell = currentCell.next;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int hash = key != null ? key.hashCode() : 0;
        int index = indexFromHash(key);
        Cell<K, V> currentCell = cellArray[index];
        Cell<K, V> previousCell = null; //For re-linking cells
        V removedValue = null;

        while (currentCell != null) {
            if (currentCell.hash == hash && Objects.equals(currentCell.getKey(), key)) {
                removedValue = currentCell.getValue();
                if (previousCell == null) {
                    cellArray[index] = currentCell.next;
                } else {
                    previousCell.next = currentCell.next;
                }
                size--;
                break;
            }
            previousCell = currentCell;
            currentCell = currentCell.next;
        }
        return removedValue;
    }

    @Override
    public boolean containsKey(K key) {
        int hash = key != null ? key.hashCode() : 0;
        int index = indexFromHash(key);
        Cell<K, V> current = cellArray[index];
        while (current != null) {
            if (current.hash == hash && Objects.equals(current.getKey(), key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        for (Cell<K, V> cell : cellArray) {
            if (cell != null) {
                Cell<K, V> current = cell;
                while (current != null) {
                    if (Objects.equals(current.getValue(), value)) {
                        return true;
                    }
                    current = current.next;
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
    public int capacity() {
        return capacity;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
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

        public CustomIterator() {
            currentCellIndex = 0;
            currentCell = null;
        }

        @Override
        public boolean hasNext() {
            // Check the current cell first
            if (currentCell != null && currentCell.next != null) {
                return true;
            }

            // Move to the next non-empty cell (bucket)
            for (int i = currentCellIndex; i < capacity; i++) {
                if (cellArray[i] != null) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Cell<K, V> next() {
            // If the current cell (bucket) has more elements, return the next one
            if (currentCell != null && currentCell.next != null) {
                currentCell = currentCell.next;
                return currentCell;
            }

            // Otherwise, find the next non-empty cell (bucket)
            while (currentCellIndex < capacity) {
                if (cellArray[currentCellIndex] != null) {
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
