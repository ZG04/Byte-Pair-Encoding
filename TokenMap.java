/**
 * This class is implemented for you! (Provided you provide an implementation of an ArrayList)
 *
 * <p>See {@link FileHelper} for methods to load this class from a file
 *
 * It is intended to provide a mapping mechanism from Pairs of EncodingValues to tokens
 * and the reverse mapping from {@link TokenValue} to {@link Pair}s of {@link EncodingValue}s
 */
public class TokenMap {
    private EncodingValueMap<EncodingValueMap<TokenValue>> forwardMap;

    private EncodingValueMap<Pair> backwardMap; // we don't need the byte values so this could be optimized

    public TokenMap() {
        forwardMap = new EncodingValueMap<>();
        backwardMap = new EncodingValueMap<>();
    }

    /**
     * Hopefully you shouldn't need this function (unless you want to manually make a TokenMap for testing)
     * @param first
     * @param second
     * @param token
     */
    public void addToken(EncodingValue first, EncodingValue second, TokenValue token) {
        EncodingValueMap<TokenValue> firstLayer = forwardMap.get(first);
        if (firstLayer == null) {
            firstLayer = new EncodingValueMap<>();
            forwardMap.set(first, firstLayer);
        }
        firstLayer.set(second, token);

        backwardMap.set(token, new Pair(first, second));
    }

    /**
     * the forward mapping from a token pair
     * 
     * @param first
     * @param second
     * @return token or null
     */
    public TokenValue getToken(EncodingValue first, EncodingValue second) {
        EncodingValueMap<TokenValue> firstLayer = forwardMap.get(first);
        if (firstLayer == null) {
            return null;
        }
        return firstLayer.get(second);
    }

    /**
     * the backward mapping from a token to token pair
     *
     * @param token
     * @return token or null
     */
    public Pair getPair(TokenValue token) {
        return backwardMap.get(token);
    }

    /**
     * Simple sanity check to make sure files are saved correctly
     * @return whehter the backward Map is complete or not
     */
    protected boolean backwardMapValid() {
        for (short i = 0; i < backwardMap.numTokens(); i++) {
            Pair p = backwardMap.get(new TokenValue(i));
            if (p == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the number of tokens in the map
     */
    public int numTokens() {
        return backwardMap.numTokens();
    }
}

/**
 * The inner mechanism of how {@code TokenMap} works
 * <p> domain is always {@code EncodingValue} types
 * @param <T> the codomain of the map
 */
class EncodingValueMap<T> {
    private static final int BYTE_VALUES = 256;
    private T[] byteMap;
    private ArrayList<T> tokenMap;

    @SuppressWarnings("unchecked")
    public EncodingValueMap() {
        byteMap = (T[]) new Object[BYTE_VALUES];
        tokenMap = new ArrayList<>();
    }

    /**
     * @param key
     * @return value stored at key or null if does not exist
     */
    public T get(EncodingValue key) {
        if (key instanceof ByteValue) {
            ByteValue key_bv = (ByteValue) key;
            int index = key_bv.value();
            index &= 0xff;
            return byteMap[index];
        } else if (key instanceof TokenValue) {
            short token = ((TokenValue) key).value();
            if (token >= tokenMap.size()) {
                return null;
            }
            return tokenMap.get(token);
        } else {
            throw new AssertionError("EncodingValue expected to only be implemented by ByteValue and TokenValue");
        }
    }

    public void set(EncodingValue key, T value) {
        if (key instanceof ByteValue) {
            ByteValue key_bv = (ByteValue) key;
            int index = key_bv.value();
            index &= 0xff;
            byteMap[index] = value;
        } else if (key instanceof TokenValue) {
            short token = ((TokenValue) key).value();
            tokenMap.ensureCapacity(token);
            while (token >= tokenMap.size()) {
                tokenMap.add(null);
            }
            tokenMap.set(token, value);
        } else {
            throw new AssertionError("EncodingValue expected to only be implemented by ByteValue and TokenValue");
        }
    }

    public int numBytes() {
        return BYTE_VALUES;
    }

    public int numTokens() {
        return tokenMap.size();
    }
}
