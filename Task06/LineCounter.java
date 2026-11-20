import java.io.*;
import java.nio.file.*;

public class LineCounter {
    public static long countUnbuffered(Path file) throws IOException {
        long lines = 0;
        try (InputStream in = Files.newInputStream(file)) {
            int value;
            while ((value = in.read()) != -1) if (value == '\n') lines++;
        }
        return lines;
    }
    public static long countBuffered(Path file) throws IOException {
        long lines = 0;
        byte[] buffer = new byte[64 * 1024];
        try (InputStream in = new BufferedInputStream(Files.newInputStream(file), buffer.length)) {
            int count;
            while ((count = in.read(buffer)) != -1)
                for (int i = 0; i < count; i++) if (buffer[i] == '\n') lines++;
        }
        return lines;
    }
    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile("attendance", ".txt");
        Files.writeString(file, "A\nB\nC\n");
        System.out.println("Unbuffered lines: " + countUnbuffered(file));
        System.out.println("Buffered lines: " + countBuffered(file));
        Files.deleteIfExists(file);
    }
}
