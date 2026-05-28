import java.io.*;
import java.util.Scanner;

public class Main {
    private static void usage() {
        System.out.println("Usage:");
        System.out.println("  Encode text: java Main encode text <cover.txt> <secret message> <out.txt>");
        System.out.println("  Decode text: java Main decode text <encoded.txt>");
        System.out.println("  Encode image: java Main encode image <cover.png> <secret message> <out.png>");
        System.out.println("  Decode image: java Main decode image <encoded.png>");
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            usage();
            return;
        }
        String action = args[0];
        String mode = args[1];
        try {
            if ("encode".equalsIgnoreCase(action)) {
                if (args.length < 5) {
                    // allow interactive message input
                    System.out.println("Enter secret message (single line):");
                    Scanner sc = new Scanner(System.in);
                    String message = sc.nextLine();
                    String cover = args[2];
                    String out = args[3];
                    if ("text".equalsIgnoreCase(mode)) {
                        TextSteg.encode(new File(cover), message, new File(out));
                        System.out.println("Wrote encoded text to " + out);
                    } else {
                        ImageSteg.encode(new File(cover), message, new File(out));
                        System.out.println("Wrote encoded image to " + out);
                    }
                } else {
                    String cover = args[2];
                    String message = args[3];
                    String out = args[4];
                    if ("text".equalsIgnoreCase(mode)) {
                        TextSteg.encode(new File(cover), message, new File(out));
                        System.out.println("Wrote encoded text to " + out);
                    } else if ("image".equalsIgnoreCase(mode)) {
                        ImageSteg.encode(new File(cover), message, new File(out));
                        System.out.println("Wrote encoded image to " + out);
                    } else {
                        usage();
                    }
                }
            } else if ("decode".equalsIgnoreCase(action)) {
                String file = args[2];
                if ("text".equalsIgnoreCase(mode)) {
                    String secret = TextSteg.decode(new File(file));
                    if (secret == null) System.out.println("No hidden message found.");
                    else System.out.println("Hidden message:\n" + secret);
                } else if ("image".equalsIgnoreCase(mode)) {
                    String secret = ImageSteg.decode(new File(file));
                    if (secret == null) System.out.println("No hidden message found.");
                    else System.out.println("Hidden message:\n" + secret);
                } else {
                    usage();
                }
            } else {
                usage();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
