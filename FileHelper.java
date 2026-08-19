import java.io.*;
import java.util.List;

public class FileHelper {
    private static final String BYTE_PAIR_TOKEN_MAGIC = "BPET";
    public static void storeTokens(TokenMap map, String filename) {
        try {
            storeTokens(map, new PrintStream(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filename);
        }
    }
    public static void storeTokens(TokenMap map, PrintStream output) {
        if (!map.backwardMapValid()) {
            throw new AssertionError("Received map is invalid, cannot be serialized");
        }
        // could probably have a more involved header than just the magic but this is fine
        output.print(BYTE_PAIR_TOKEN_MAGIC);
        int mapLength = map.numTokens();
        for (short i = 0; i < mapLength; i++) {
            Pair p = map.getPair(new TokenValue(i));
            try {
                rawOutBPET(p.first(), output);
                rawOutBPET(p.second(), output);
            } catch (IOException e) {
                throw new RuntimeException("Got IOException: " + e);
            }
        }
    }

    private static final short SHORT_MSB = (short) 0x8000;
    public static TokenMap loadTokens(String filename) {
        try {
            return loadTokens(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filename);
        }
    }
    public static TokenMap loadTokens(InputStream input) {
        TokenMap result = new TokenMap();
        byte[] buff = new byte[4];
        if (readWrapped(input, buff) != 4 || !new String(buff).equals(BYTE_PAIR_TOKEN_MAGIC)) {
            throw new RuntimeException("Invalid file type");
        }
        short tokenValue = 0;
        byte[] firstBuff = new byte[2];
        byte[] secondBuff = new byte[2];
        while (readWrapped(input, buff) == 4 && tokenValue >= 0) {
            firstBuff[0] = buff[0];
            firstBuff[1] = buff[1];
            secondBuff[0] = buff[2];
            secondBuff[1] = buff[3];
            short firstVal = shortFromBigEndian(firstBuff);
            short secondVal = shortFromBigEndian(secondBuff);
            EncodingValue first;
            EncodingValue second;
            // could put these if statements into a function
            if (firstVal >= 0) {
                first = new ByteValue((byte) firstVal);
            } else {
                firstVal &= ~SHORT_MSB;
                first = new TokenValue((short) firstVal);
            }
            if (secondVal >= 0) {
                second = new ByteValue((byte) secondVal);
            } else {
                secondVal &= ~SHORT_MSB;
                second = new TokenValue((short) secondVal);
            }
            TokenValue token = new TokenValue(tokenValue++);
            result.addToken(first, second, token);
        }
        return result;
    }

    private static final String BYTE_PAIR_RUN_LENGTH_MAGIC = "BPRL";
    public static void storeEncodedList(EncodedList list, String filename) {
        try {
            storeEncodedList(list, new PrintStream(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filename);
        }
    }
    private static final int MAX_RUN_LENGTH = Byte.MAX_VALUE;
    public static void storeEncodedList(EncodedList list, PrintStream output) {
        output.print(BYTE_PAIR_RUN_LENGTH_MAGIC);

        final int size = list.size();
        if (size == 0) {
            return;
        }
        int i = 0;
        int runLength = 0;
        byte[] runData = new byte[MAX_RUN_LENGTH * 2];
        EncodingValue curr;
        while (i < size) {
            while (i < size && (curr = list.get(i)) instanceof TokenValue && runLength < MAX_RUN_LENGTH) {
                byte[] valueBytes = bigEndianShort(((TokenValue) curr).value());
                runData[runLength * 2] = valueBytes[0];
                runData[runLength * 2 + 1] = valueBytes[1];
                i++;
                runLength++;
            }
            output.write(runLength);
            output.write(runData, 0, runLength * 2);
            runLength = 0;

            if (i >= size) {
                break;
            }
            while (i < size && (curr = list.get(i)) instanceof ByteValue && runLength < MAX_RUN_LENGTH) {
                byte valueByte = ((ByteValue) curr).value();
                runData[runLength] = valueByte;
                i++;
                runLength++;
            }
            output.write(runLength);
            output.write(runData, 0, runLength);
            runLength = 0;
        }
    }

    public static void appendEncodedList(EncodedList list, String sourceFile) {
        try {
            appendEncodedList(list, new FileInputStream(sourceFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + sourceFile);
        }
    }
    public static void appendEncodedList(EncodedList list, InputStream source) {
        byte[] buff = new byte[MAX_RUN_LENGTH * 2];
        byte[] valBuff = new byte[2];
        int bytesRead = readWrapped(source, buff, 4);
        if (bytesRead != 4 || !new String(buff, 0, 4).equals(BYTE_PAIR_RUN_LENGTH_MAGIC)) {
            throw new RuntimeException("Invalid file type");
        }
        int length;
        while (true) {
            // token values
            length = readWrapped(source);
            if (length == -1) {
                break;
            }
            bytesRead = readWrapped(source, buff, 2 * length);
            if (bytesRead != length * 2) {
                throw new AssertionError("File formatting error");
            }
            for (int i = 0; i < length; i++) {
                valBuff[0] = buff[2 * i];
                valBuff[1] = buff[2 * i + 1];
                short token = shortFromBigEndian(valBuff);
                list.add(new TokenValue(token));
            }

            // byte values
            length = readWrapped(source);
            if (length == -1) {
                break;
            }
            bytesRead = readWrapped(source, buff, length);
            if (bytesRead != length) {
                throw new AssertionError("File formatting error");
            }
            for (int i = 0; i < length; i++) {
                list.add(new ByteValue(buff[i]));
            }
        }
    }

    public static void appendFile(EncodedList list, String sourceFile) {
        try {
            appendFile(list, new FileInputStream(sourceFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + sourceFile);
        }
    }
    public static void appendFile(EncodedList list, InputStream source) {
        // none of our files are that big, but should probably consider a reasonable sized buffer though
        byte[] sourceBytes;
        try {
            sourceBytes = source.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        list.addBytes(sourceBytes);
    }
    public static String bytesToString(byte[] bytes) {
        return new String(bytes);
    }
    public static String bytesToString(Byte[] bytes) {
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i];
        }
        return bytesToString(result);
    }
    public static String bytesToString(List<Byte> bytes) {
        int size = bytes.size();
        byte[] result = new byte[size];
        int resultSize = 0;
        for (Byte b: bytes) {
            result[resultSize++] = b;
        }
        if (resultSize != size) {
            throw new AssertionError("Size mismatch after copy");
        }
        return bytesToString(result);
    }

    private static int readWrapped(InputStream input, byte[] buff) {
        try {
            return input.read(buff);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static int readWrapped(InputStream input, byte[] buff, int length) {
        try {
            return input.read(buff, 0, length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static int readWrapped(InputStream input) {
        try {
            return input.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void rawOutBPET(EncodingValue v, PrintStream output) throws IOException {
        if (v instanceof TokenValue) {
            rawOutBPET((TokenValue) v, output);
        }
        else if (v instanceof ByteValue) {
            rawOutBPET((ByteValue) v, output);
        }
        else {
            throw new AssertionError("Invalid EncodingValue Type");
        }
    }


    private static void rawOutBPET(TokenValue v, PrintStream output) throws IOException {
        short value = v.value();
        if (value < 0)
            throw new AssertionError("Token must be positive");
        value |= SHORT_MSB;
        output.write(bigEndianShort(value));
    }

    private static void rawOutBPET(ByteValue b, PrintStream output) throws IOException {
        output.write(0);
        output.write(b.value());
    }

    private static byte[] bigEndianShort(short s) {
        byte[] result = new byte[2];
        result[0] = (byte) (s >> 8);
        result[1] = (byte) s;
        return result;
    }

    private static short shortFromBigEndian(byte[] bytes) {
        if (bytes.length != 2) {
            throw new IllegalArgumentException("A short must be 2 bytes");
        }
        short result = (short) ((bytes[0] & 0xff) << 8);
        result |= bytes[1] & 0xff;
        return result;
    }
}
