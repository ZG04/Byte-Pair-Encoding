import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public interface SimpleList<T> extends List<T> {
    /**
     * Increases the capacity of this {@code SimpleList}, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    public void ensureCapacity(int minCapacity);

    @Override
    default public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    default public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    default public boolean remove(Object o) {
        int index = indexOf(o);
        if (index == -1) {
            return false;
        }
        remove(index);
        return true;
    }

    @Override
    default public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    default public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        int addedSize = c.size();
        int totalSize = this.size();
        this.ensureCapacity(totalSize);
        for (T o: c) {
            add(o); // add(o) always returns true for list interfaces
            changed = true;
        }
        return changed;
    }

    @Override
    default public boolean addAll(int index, Collection<? extends T> c) {
        boolean changed = false;
        for (T o: c) {
            add(index, o); // add(i, o) always returns true for list interfaces
            changed = true;
        }
        return changed;
    }

    @Override
    default public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o: c) {
            if (remove(o)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    default public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        // this is a really lazy and slow implementation
        for (T o: this) {
            if (!c.contains(o)) {
                remove(o);
            }
        }
        return changed;
    }

    @Override
    default public int lastIndexOf(Object o) {
        // you already implemented indexOf, you can do lastIndexOf!
        // I believe in you!
        throw new UnsupportedOperationException("lastIndexOf(Object) not supported with ArrayList.SimpleList interface");
    }

    @Override
    default public ListIterator<T> listIterator() {
        // we just don't need this for our class; iterator is sufficient
        throw new UnsupportedOperationException("listIterator() not supported with ArrayList.SimpleList interface");
    }

    @Override
    default public ListIterator<T> listIterator(int index) {
        // we just don't need this for our class; iterator is sufficient
        throw new UnsupportedOperationException("listIterator(index) not supported with ArrayList.SimpleList interface");
    }

    @Override
    default public List<T> subList(int fromIndex, int toIndex) {
        // we just don't need this for our class
        throw new UnsupportedOperationException("listIterator(index) not supported with ArrayList.SimpleList interface");
    }

    @Override
    default public <V> V[] toArray(V[] a) {
        throw new UnsupportedOperationException("toArray(V[]) not supported with ArrayList.SimpleList interface");
    }
}
