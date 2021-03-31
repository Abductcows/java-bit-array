package gr.geompokon.bitarray;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class BitArray extends AbstractList<Integer> implements RandomAccess {

    BitArrayImpl array;

    BitArray() {
        array = new BitArrayImpl();
    }
    BitArray(int initialSize) {
        array = new BitArrayImpl(initialSize);
    }

    @Override
    public boolean add(Integer bit) {
        return array.add(bit);
    }

    @Override
    public Integer set(int index, Integer bit) {
        return array.set(index, bit);
    }

    @Override
    public void add(int index, Integer bit) {
        array.add(index, bit);
    }

    @Override
    public Integer remove(int index) {
        return array.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        Iterator<Integer> it = this.iterator();
        int index = 0;
        while (it.hasNext()) {
            if (it.next().equals(o)) {
                return index;
            }
            ++index;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        Iterator<Integer> it = this.iterator();
        int index = 0, lastIndex = -1;
        while (it.hasNext()) {
            if (it.next().equals(o)) {
                lastIndex = index;
            }
            ++index;
        }
        return lastIndex;
    }

    @Override
    public void clear() {
        // TODO: add clear method to BitArrayImpl
        super.clear();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Integer> c) {
        return super.addAll(index, c);
    }

    @Override
    public Iterator<Integer> iterator() {
        return new BitIterator(this);
    }

    @Override
    public ListIterator<Integer> listIterator() {
        // TODO
        return super.listIterator();
    }

    @Override
    public ListIterator<Integer> listIterator(int index) {
        // TODO
        return super.listIterator(index);
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
        return super.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object o) {
        // TODO: can uncomment when listIterator is implemented
        // return super.equals(o);

        return this == o;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        // TODO
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean isEmpty() {
        return array.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Object[] toArray() {
        // TODO
        return super.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO
        return super.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        // TODO
        return super.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return super.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        return super.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return super.retainAll(c);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void replaceAll(UnaryOperator<Integer> operator) {
        // TODO: implement with ListIterator?
    }

    @Override
    public void sort(Comparator<? super Integer> c) {
        // TODO
    }

    @Override
    public Spliterator<Integer> spliterator() {
        return null;
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        // TODO
        return null;
    }

    @Override
    public boolean removeIf(Predicate<? super Integer> filter) {
        return false;
    }

    @Override
    public Stream<Integer> stream() {
        // TODO
        return null;
    }

    @Override
    public Stream<Integer> parallelStream() {
        // TODO
        return null;
    }

    @Override
    public void forEach(Consumer<? super Integer> action) {
        Iterator<Integer> it = this.iterator();
        it.forEachRemaining(action);
    }

    @Override
    public Integer get(int index) {
        return array.get(index);
    }

    @Override
    public int size() {
        return array.size();
    }

    private class BitIterator implements Iterator<Integer> {
        int currentIndex;
        BitArray parent;

        BitIterator(BitArray parent) {
            currentIndex = 0;
            this.parent = parent;
        }

        @Override
        public boolean hasNext() {
            return currentIndex < parent.size();
        }

        @Override
        public Integer next() {
            return array.get(currentIndex++);
        }

        @Override
        public void forEachRemaining(Consumer<? super Integer> action) {
            while (hasNext()) {
                Integer i = next();
                action.accept(i);
            }
        }
    }
}
