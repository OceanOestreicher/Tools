package custom.striker.service;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Service which handles loading images from the resource folder. Images must be stored in a file called
 * images
 */
public final class ImageService {

    private static final Map<String, ImageIcon> IMAGE_CACHE = new HashMap<>();

    /**
     * Gets an ImageIcon of the provided file with a default scale of 1
     * @param fileName Image file name
     * @return ImageIcon of the file with a scale of 1
     * @throws ImageException if the file or images directory does not exist
     */
    public static ImageIcon getImageIcon(String fileName) {
        return getImageIcon(fileName, 1);
    }

    /**
     * Returns a scaled ImageIcon of the provided file
     * @param fileName Image file name
     * @param scale Scale of the image
     * @return Scaled ImageIcon
     * @throws ImageException if the file or images directory does not exist
     */
    public static ImageIcon getImageIcon(String fileName, double scale) {
        if (IMAGE_CACHE.get(fileName + scale) != null) {
            return IMAGE_CACHE.get(fileName + scale);
        }

        URL imageUrl = ImageService.class.getResource("/images/" + fileName);
        if ( imageUrl == null) {
            throw new ImageException("Unable to load image from file: " + fileName);
        }

        ImageIcon imageIcon = new ImageIcon(imageUrl);
        Image originalImage = imageIcon.getImage();
        int originalWidth = imageIcon.getIconWidth();
        int originalHeight = imageIcon.getIconHeight();
        Image resizedImage = originalImage.getScaledInstance((int) (originalWidth * scale), (int) (originalHeight * scale), Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        IMAGE_CACHE.put(fileName + scale, resizedIcon);

        return resizedIcon;
    }
}
