# Text Steganography System (Java)

This project implements a simple text/image steganography system in Java.

Features
- Encode and decode secret text into a text cover using zero-width characters (visible content unchanged).
- Encode and decode secret text into a PNG image using LSB steganography.

Compile

```bash
javac src/*.java -d out
```

Run

Text encode:

```bash
java -cp out Main encode text cover.txt "secret message" out.txt
```

Text decode:

```bash
java -cp out Main decode text out.txt
```

Image encode (PNG):

```bash
java -cp out Main encode image cover.png "secret message" out.png
```

Image decode:

```bash
java -cp out Main decode image out.png
```

Notes
- The text mode appends invisible characters to the file; the visible text remains unchanged when viewed normally.
- The image mode writes an encoded PNG; the image will look the same to the eye but its pixel LSBs will contain data.
- Ensure cover file has enough capacity (image dimensions or text size) for the secret message.
