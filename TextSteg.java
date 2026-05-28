import java.io.*;
import java.nio.charset.StandardCharsets;

public class TextSteg {
    private static final char ONE = '\u200B'; // zero-width space
    private static final char ZERO = '\u200C'; // zero-width non-joiner
    private static final String DELIM = "\u2063"; // invisible separator

    public static void encode(File coverFile, String secretMessage, File outFile) throws IOException {
        String cover = readAll(coverFile);
        byte[] msgBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
        int len = msgBytes.length;
        StringBuilder bits = new StringBuilder();
        // 32-bit length prefix
        for (int i = 31; i >= 0; i--) {
            bits.append(((len >> i) & 1) == 1 ? ONE : ZERO);
        }
        // message bytes
        for (byte b : msgBytes) {
            for (int i = 7; i >= 0; i--) {
                bits.append(((b >> i) & 1) == 1 ? ONE : ZERO);
            }
        }

        String out = cover + DELIM + bits.toString();
        try (Writer w = new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8)) {
            w.write(out);
        }
    }

    public static String decode(File encodedFile) throws IOException {
        String s = readAll(encodedFile);
        int idx = s.indexOf(DELIM);
        if (idx == -1) return null;
        String tail = s.substring(idx + DELIM.length());
        // convert tail of zero-width chars into bits
        StringBuilder bitStr = new StringBuilder();
        for (int i = 0; i < tail.length(); i++) {
            char c = tail.charAt(i);
            if (c == ONE) bitStr.append('1');
            else if (c == ZERO) bitStr.append('0');
            // ignore any other chars
        }
        if (bitStr.length() < 32) return null;
        int len = 0;
        for (int i = 0; i < 32; i++) {
            len = (len << 1) | (bitStr.charAt(i) - '0');
        }
        int totalBits = 32 + len * 8;
        if (bitStr.length() < totalBits) return null;
        byte[] msg = new byte[len];
        for (int i = 0; i < len; i++) {
            int val = 0;
            for (int j = 0; j < 8; j++) {
                val = (val << 1) | (bitStr.charAt(32 + i * 8 + j) - '0');
            }
            msg[i] = (byte) val;
        }
        return new String(msg, StandardCharsets.UTF_8);
    }

    private static String readAll(File f) throws IOException {
        byte[] bytes = java.nio.file.Files.readAllBytes(f.toPath());
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
