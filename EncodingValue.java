// expected to only be implemented by ByteValue and TokenValue
abstract class EncodingValue {

    public String expandAsString(TokenMap map) {
        ArrayList<Byte> bytes = expand(map);
        return FileHelper.bytesToString(bytes);
    }

    public ArrayList<Byte> expand(TokenMap map) {
        ArrayList<Byte> result = new ArrayList<>();
        expand(map, result);
        return result;
    }
    // This must be overriden by TokenMap and ByteMap
    // I've provided those methods
    abstract protected void expand(TokenMap map, ArrayList<Byte> result);

    abstract public String toString();

    abstract public boolean equals(Object obj);
}

class TokenValue extends EncodingValue {
    private short value;

    public TokenValue(short value) {
        this.value = value;
    }

    public short value() {
        return value;
    }

    @Override
    protected void expand(TokenMap map, ArrayList<Byte> result) {
        Pair p = map.getPair(this);
        p.first().expand(map, result);
        p.second().expand(map, result);
    }

    @Override
    public String toString() {
        return String.format("<%d>", value);
    }

    @Override
    // equivalent to auto generated, but without import
    public int hashCode() {
        return Short.valueOf(value).hashCode();
    }

    @Override
    // equivalent to auto generated
    public boolean equals(Object o) {
        if (!(o instanceof TokenValue)) return false;
        TokenValue other = (TokenValue) o;
        return value == other.value;
    }
}

class ByteValue extends EncodingValue {
    private byte value;

    public ByteValue(byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }

    public short unsignedValue() {
        return (short)(((short) value) & 0xff);
    }

    @Override
    protected void expand(TokenMap map, ArrayList<Byte> result) {
        result.add(value);
    }

    @Override
    public String toString() {
        if (value >= 0) {
            return String.format("[%d: %c]", value, value);
        }
        else {
            return String.format("[%d: \\x%x]", unsignedValue(), value);
        }
    }

    @Override
    // equivalent to auto generated, but without import
    public int hashCode() {
        return Byte.valueOf(value).hashCode();
    }

    @Override
    // equivalent to auto generated
    public boolean equals(Object o) {
        if (!(o instanceof ByteValue)) return false;
        ByteValue byteValue = (ByteValue) o;
        return value == byteValue.value;
    }
}

// this could be a record, but not doing that since that's a "new" feature & more syntax
class Pair {
    private final EncodingValue first;
    private final EncodingValue second;

    public Pair(EncodingValue first, EncodingValue second) {
        this.first = first;
        this.second = second;
    }

    public EncodingValue first() {
        return first;
    }

    public EncodingValue second() {
        return second;
    }

    @Override
    // auto generated
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }

    @Override
    // equivalent to auto generated, but without import
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pair other = (Pair) o;
        return this.first.equals(other.first) && this.second.equals(other.second);
    }

    @Override
    // equivalent to auto generated
    // I know it's funky, just ignore it for now
    public int hashCode() {
        return 31 * first.hashCode() + second.hashCode();
    }

}
