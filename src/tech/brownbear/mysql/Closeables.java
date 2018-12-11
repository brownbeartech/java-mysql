package tech.brownbear.mysql;

public class Closeables {
    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch(Exception e) {
                // Ignore silently
            }
        }
    }
}