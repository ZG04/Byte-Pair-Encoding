import java.util.Iterator;

class EncodedList {

    private ArrayList<EncodingValue> data;
    private TokenMap map;

    public EncodedList(TokenMap map) {

        this.map = map;
        this.data = new ArrayList<>();

    }
    /**
     * Increases the capacity of this {@code SimpleList}, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    public void ensureCapacity(int minCapacity) {

        data.ensureCapacity(minCapacity);

    }

    /**
     * Returns the number of elements in this list.  If this list contains
     * more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of elements in this list
     */
    public int size() {

        return data.size();

    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    public Iterator<EncodingValue> iterator() {

        return data.iterator();

    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must
     * allocate a new array even if this list is backed by an array).
     * The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all the elements in this list in proper
     * sequence
     */
    public Object[] toArray() {

        return data.toArray();

    }

    /**
     * IMPORTANT: This method must maintain the EncodedList!
     * Any time this method is called, the list must be fully encoded.
     * Additionally, if there are multiple pairs that can be encoded,
     * you should use the pairs that map to the *least* token value
     * When token value is the same, you should encode from right-to-left.
     *
     * Appends the specified element to the end of this list.
     *
     * <p>Lists that support this operation may place limitations on what
     * elements may be added to this list.  In particular, some
     * lists will refuse to add null elements, and others will impose
     * restrictions on the type of elements that may be added.  List
     * classes should clearly specify in their documentation any restrictions
     * on what elements may be added.
     *
     * @param element element to be appended to this list
     * @return {@code true} (as specified by {@link java.util.Collection#add})
     */
    public boolean add(EncodingValue element) {

        add(size(), element);
        return true;

    }

    /**
     * Removes all elements from this list (optional operation).
     * The list will be empty after this call returns.
     *
     */
    public void clear() {

        data.clear();

    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   ({@code index < 0 || index >= size()})
     */
    public EncodingValue get(int index) {

        return data.get(index);

    }

    /**
     * IMPORTANT: This method must maintain the EncodedList!
     * Any time this method is called, the list must be fully encoded.
     * Additionally, if there are multiple pairs that can be encoded,
     * you should use the pairs that map to the *least* token value
     * When token value is the same, you should encode from right-to-left.
     *
     * <p>Replaces the element at the specified position in this list with the
     * specified element (optional operation).
     *
     * @param index   index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       ({@code index < 0 || index >= size()})
     */
    public EncodingValue set(int index, EncodingValue element) {

        EncodingValue old = data.set(index, element);
        createPairs();
        return old;

    }

    /**
     * IMPORTANT: This method must maintain the EncodedList!
     * Any time this method is called, the list must be fully encoded.
     * Additionally, if there are multiple pairs that can be encoded,
     * you should use the pairs that map to the *least* token value
     * When token value is the same, you should encode from right-to-left.
     *
     * <p>Inserts the specified element at the specified position in this list
     * (optional operation).  Shifts the element currently at that position
     * (if any) and any subsequent elements to the right (adds one to their
     * indices).
     *
     * @param index   index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       ({@code index < 0 || index > size()})
     */
    public void add(int index, EncodingValue element) {

        data.add(index, element);
        createPairs();

    }

    /**
     * IMPORTANT: This method must maintain the EncodedList!
     * Any time this method is called, the list must be fully encoded.
     * Additionally, if there are multiple pairs that can be encoded at once,
     * you should use the pairs that map to the *least* token value.
     * When token value is the same, you should encode from right-to-left.
     *
     * <p>This is a unique method to EncodedLists, allowing you to easily
     * add an array of bytes all at once to the list.
     *
     * <p>It is meant to be the equivalent to {@link java.util.List#addAll},
     * but with a simpler signature in exchange for a more complex functionality.

     * <p>The EncodedList must only be fully encoded after this method is done executed.
     * Therefore, you need to be very careful about detecting when pairs can be encoded at the same time
     * and carefully
     *
     * @param rawData
     */
    public void addBytes(byte[] rawData) {

        for(int i = 0; i < rawData.length; i++){
            data.add(new ByteValue(rawData[i]));
        }
        createPairs();
    }

    private void createPairs(){

        boolean here = true;

        while(here){

            here = false;

            TokenValue spot = null;
            int start = -1;

            for(int i = 0; i < data.size() - 1; i++) {

                EncodingValue first = data.get(i);
                EncodingValue second = data.get(i + 1);

                TokenValue token = map.getToken(first, second);

                if (token != null) {

                    if (spot == null || token.value() < spot.value() || (token.value() == spot.value() && i > start)) {

                        spot = token;
                        start = i;

                    }
                }
            }

                    if(spot != null) {
                        data.set(start, spot);
                        data.remove(start + 1);
                        here = true;

                    }
        }
    }

    /**
     * IMPORTANT: This method must maintain the EncodedList!
     * Any time this method is called, the list must be fully encoded.
     * Additionally, if there are multiple pairs that can be encoded,
     * you should use the pairs that map to the *least* token value
     * When token value is the same, you should encode from right-to-left.
     *
     * Removes the element at the specified position in this list (optional
     * operation).  Shifts any subsequent elements to the left (subtracts one
     * from their indices).  Returns the element that was removed from the
     * list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException if the {@code remove} operation
     *                                       is not supported by this list
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       ({@code index < 0 || index >= size()})
     */
    public EncodingValue remove(int index) {

        EncodingValue removed = data.remove(index);
        createPairs();
        return removed;

    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index {@code i} such that
     * {@code Objects.equals(o, get(i))},
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element
     * @throws ClassCastException   if the type of the specified element
     *                              is incompatible with this list
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *                              list does not permit null elements
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     */
    public int indexOf(Object o) {

        return data.indexOf(o);

    }

}