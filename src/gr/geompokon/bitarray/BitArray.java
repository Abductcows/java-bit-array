package gr.geompokon.bitarray;

import java.util.AbstractList;
import java.util.RandomAccess;

public class BitArray extends AbstractList<Boolean> implements RandomAccess {

    private final static int DEFAULT_CAPACITY = 10;
    private BitArrayImpl array;

    BitArray() {
        this(DEFAULT_CAPACITY);
    }

    BitArray(int initialSize) {
        array = new BitArrayImpl(initialSize);
    }

    @Override
    public void add(int index, Boolean bit) {
        array.add(index, bit);
    }

    @Override
    public Boolean get(int index) {
        return array.get(index);
    }

    @Override
    public Boolean set(int index, Boolean bit) {
        return array.set(index, bit);
    }

    @Override
    public Boolean remove(int index) {
        return array.remove(index);
    }

    @Override
    public int size() {
        return array.size();
    }

    @Override
    public void clear() {
        // TODO: implement clear in BitArrayImpl
        super.clear();
    }
}
