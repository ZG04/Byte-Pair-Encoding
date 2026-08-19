import java.util.Iterator;


public class ArrayList<T> implements SimpleList<T> {

    private T[] data;
    private int size;

    public ArrayList(int initialCapacity) {
        if(initialCapacity < 1) {
           initialCapacity = 1;
        }
        data = (T[]) new Object[initialCapacity];
        size = 0;

    }

    public ArrayList() {

        this(5000);

    }

    public ArrayList(T[] data) {

        this.data = (T[])  new Object[data.length];
        size = data.length;
        for(int i = 0; i < size; i++) {
            this.data[i] = data[i];

        }
    }

    protected int capacity() {

        return data.length;

    }

    @Override
    public void ensureCapacity(int minCapacity) {

        if (minCapacity <= data.length) {
            return;
        }

        int newCapacity = data.length * 2;

        if(newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }

        T[] newData = (T[]) new Object[newCapacity];

        for (int i = 0; i < size; i++){
            newData[i] = data[i];
        }
        data = newData;

    }

    @Override
    public int size() {

        return size;

    }

    @Override
    public void add(int index, T element) {

        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        ensureCapacity(size + 1);

        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = element;
        size++;

    }

    @Override
    public boolean add(T t) {

        ensureCapacity(size + 1);
        data[size] = t;
        size++;
        return true;

    }

    @Override
    public void clear() {

        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;

    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {

        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return data[index];
    }

    @Override
    public T set(int index, T element) {

        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        T oldSpot = data[index];
        data[index] = element;
        return oldSpot;
    }

    @Override
    public T remove(int index) {

        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        T oldSpot = data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null;
        size--;
        return oldSpot;
    }

    @Override
    public int indexOf(Object o) {

        for (int i = 0; i < size; i++) {
            if (o == null) {
                if (data[i] == null) {
                    return i;
                }
            }
            if (o.equals(data[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Iterator<T> iterator() {

        return new Iterator<T>() {
            int position = 0;

            @Override
            public boolean hasNext() {
                return position < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new IllegalStateException();
                }
                T value = data[position];
                position++;
                return value;
            }
        };
    }

    @Override
    public Object[] toArray() {

        Object[] array = new Object[size];
        for (int i = 0; i < size; i++) {
            array[i] = data[i];
        }
        return array;
    }
}
