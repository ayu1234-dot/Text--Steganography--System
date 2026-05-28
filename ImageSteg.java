import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ImageSteg {
    public static void encode(File coverImage, String secretMessage, File outFile) throws IOException {
        byte[] msgBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
        int len = msgBytes.length;
        int totalBits = 32 + len * 8;

        BufferedImage img = ImageIO.read(coverImage);
        if (img == null) throw new IOException("Unsupported image or cannot read: " + coverImage);
        int w = img.getWidth();
        int h = img.getHeight();
        int capacity = w * h * 3;
        if (totalBits > capacity) throw new IOException("Image capacity too small: need " + totalBits + " bits, have " + capacity);

        int bitIndex = 0;
        int[] bits = new int[totalBits];
        // length prefix
        for (int i = 31; i >= 0; i--) bits[bitIndex++] = ((len >> i) & 1);
        for (byte b : msgBytes) {
            for (int i = 7; i >= 0; i--) bits[bitIndex++] = ((b >> i) & 1);
        }

        bitIndex = 0;
        outer:
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int a = (rgb >> 24) & 0xFF;
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                if (bitIndex < totalBits) {
                    r = (r & 0xFE) | bits[bitIndex++];
                }
                if (bitIndex < totalBits) {
                    g = (g & 0xFE) | bits[bitIndex++];
                }
                if (bitIndex < totalBits) {
                    b = (b & 0xFE) | bits[bitIndex++];
                }
                int nrgb = (a << 24) | (r << 16) | (g << 8) | b;
                img.setRGB(x, y, nrgb);
                if (bitIndex >= totalBits) break outer;
            }
        }
        ImageIO.write(img, "png", outFile);
    }

    public static String decode(File encodedImage) throws IOException {
        BufferedImage img = ImageIO.read(encodedImage);
        if (img == null) throw new IOException("Unsupported image or cannot read: " + encodedImage);
        int w = img.getWidth();
        int h = img.getHeight();
        StringBuilder bits = new StringBuilder();
        outer:
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                bits.append((r & 1));
                bits.append((g & 1));
                bits.append((b & 1));
                // we don't know total length yet; stop condition handled later
            }
        }
        // read first 32 bits as length
        if (bits.length() < 32) return null;
        int len = 0;
        for (int i = 0; i < 32; i++) len = (len << 1) | (bits.charAt(i) - '0');
        int totalBits = 32 + len * 8;
        if (bits.length() < totalBits) return null;
        byte[] msg = new byte[len];
        for (int i = 0; i < len; i++) {
            int val = 0;
            for (int j = 0; j < 8; j++) {
                val = (val << 1) | (bits.charAt(32 + i * 8 + j) - '0');
            }
            msg[i] = (byte) val;
        }
        return new String(msg, StandardCharsets.UTF_8);
    }
}
