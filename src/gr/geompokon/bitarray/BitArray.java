package gr.geompokon.bitarray;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class BitArray extends AbstractList<Boolean> implements RandomAccess {

    BitArrayImpl array;
    private ArrayList<Boolean> reference;

    BitArray() {
        array = new BitArrayImpl();
    }
    BitArray(int initialSize) {
        array = new BitArrayImpl(initialSize);
    }

    @Override
    public boolean add(Boolean bit) {
        return array.add(bit);
    }

    @Override
    public Boolean set(int index, Boolean bit) {
        return array.set(index, bit);
    }

    @Override
    public void add(int index, Boolean bit) {
        array.add(index, bit);
    }

    @Override
    public Boolean remove(int index) {
        return array.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        Iterator<Boolean> it = this.iterator();
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
        Iterator<Boolean> it = this.iterator();
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
    public boolean addAll(int index, Collection<? extends Boolean> c) {
        // TODO
        return super.addAll(index, c);
    }

    @Override
    public Iterator<Boolean> iterator() {
        return new BitListIterator();
    }

    @Override
    public ListIterator<Boolean> listIterator() {
        // TODO
        return super.listIterator();
    }

    @Override
    public ListIterator<Boolean> listIterator(int index) {
        // TODO
        return super.listIterator(index);
    }

    @Override
    public List<Boolean> subList(int fromIndex, int toIndex) {
        // TODO
        return super.subList(fromIndex, toIndex);
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
        // TODO
        return super.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Boolean> c) {
        // TODO
        return super.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO
        return super.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<Boolean> operator) {
        // TODO: implement with ListIterator?
    }

    @Override
    public void sort(Comparator<? super Boolean> c) {
        // TODO
    }

    @Override
    public Spliterator<Boolean> spliterator() {
        // TODO
        return null;
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        // TODO
        return null;
    }

    @Override
    public boolean removeIf(Predicate<? super Boolean> filter) {
        // TODO
        return false;
    }

    @Override
    public Stream<Boolean> stream() {
        // TODO
        return null;
    }

    @Override
    public Stream<Boolean> parallelStream() {
        // TODO
        return null;
    }

    @Override
    public void forEach(Consumer<? super Boolean> action) {
        Iterator<Boolean> it = this.iterator();
        it.forEachRemaining(action);
    }

    @Override
    public Boolean get(int index) {
        return array.get(index);
    }

    @Override
    public int size() {
        return array.size();
    }

    private class BitListIterator implements ListIterator<Boolean> {

        int currentIndex = -1;
        boolean previousCalled = false, nextCalled = false;

        @Override
        public boolean hasNext() {
            return currentIndex + 1 < array.size();
        }

        @Override
        public Boolean next() {
            if (hasNext()) {
                nextCalled = true;
                previousCalled = false;
                return array.get(++currentIndex);
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public boolean hasPrevious() {
            return currentIndex >= 0;
        }

        @Override
        public Boolean previous() {
            if (hasPrevious()) {
                previousCalled = true;
                nextCalled = false;
                return array.get(currentIndex--);
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return currentIndex + 1;
        }

        @Override
        public int previousIndex() {
            return currentIndex;
        }

        @Override
        public void remove() {
            if (nextCalled) {
                array.remove(currentIndex + 1);
                nextCalled = false;
            } else if (previousCalled) {
                array.remove(currentIndex);
                currentIndex = currentIndex - 1;
            } else {
                throw new IllegalStateException("remove called with no element selected");
            }
        }

        @Override
        public void set(Boolean aBoolean) {
            if (nextCalled) {
                array.set(currentIndex + 1, aBoolean);
                nextCalled = false;
            } else if (previousCalled) {
                array.set(currentIndex, aBoolean);
            } else {
                throw new IllegalStateException("remove called with no element selected");
            }
        }

        @Override
        public void add(Boolean aBoolean) {
            array.add(currentIndex + 1, aBoolean);
        }
    }
}
