package custom.striker.service;

/**
 * Exception thrown during File IO operations
 */
public class FileIOException extends RuntimeException {
    public FileIOException(String message) {
        super(message);
    }

    public FileIOException(Exception e) {
        super(e);
    }

    public FileIOException(String message, Exception e) {
        super(message, e);
    }
}
