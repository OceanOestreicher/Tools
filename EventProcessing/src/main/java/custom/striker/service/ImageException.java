package custom.striker.service;

/**
 * Exception thrown during loading and scaling of images
 */
public class ImageException extends RuntimeException {
    public ImageException(String message) {
        super(message);
    }
}
