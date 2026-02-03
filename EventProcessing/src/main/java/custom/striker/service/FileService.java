package custom.striker.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service which handles saving and loading objects from the file system. This service makes use of the
 * Jackson ObjectMapper for saving and loading.
 */
public final class FileService {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Saves the object to a file and directory. If the directory does not exist, it will be created
     *
     * @param savePath The path and file to save the object to. The path must not begin with /
     * @param objectToSave The object to save
     * @throws FileIOException thrown if an exception occurs when saving the object or if the directory does not exist
     */
    public static void save(String savePath, Object objectToSave) throws Exception {

        int indexOfFinalSlash = savePath.lastIndexOf("/");
        String directory = savePath.substring(0, indexOfFinalSlash);
        getDirectory(directory);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(savePath))) {
            String objectString = mapper.writeValueAsString(objectToSave);
            writer.write(objectString);
        } catch (Exception e) {
            throw new FileIOException("Error saving file for path: " + savePath, e);
        }
    }

    /**
     * Loads all files from the provided directory path
     *
     * @param directoryPath Directory which contains files to load. The path must not begin with /
     * @param classToLoad The class used by Jackson to convert the contents of each file to
     * @return List of objects converted from the files
     * @param <T> The class type that the files map to
     * @throws FileIOException Thrown if the directory doesn't exist or if an error occurs while loading files
     */
    public static <T> List<T> load(String directoryPath, Class<T> classToLoad) throws FileIOException {
        Path directory = getDirectory(directoryPath);

        try(Stream<Path> fileStream = Files.list(directory)) {

            List<Path> files = fileStream
                    .filter(Files::isRegularFile)
                    .toList();

            List<T> results = new ArrayList<>();

            for(Path path: files) {
                T object = mapper.readValue(path.toFile(), classToLoad);
                results.add(object);
            }

            return results;
        } catch (Exception e) {
            throw new FileIOException("Error when loading files from directory: " + directoryPath, e);
        }
    }

    /**
     * Deletes the file indicated by the file path
     * @param filePath Path to the file that should be deleted. The path must not begin with /
     * @throws FileIOException Thrown if the directory for the file doesn't exist or if an error occurs during file deletion
     */
    public static void delete(String filePath) throws FileIOException {
        int indexOfFinalSlash = filePath.lastIndexOf("/");
        String fileName = filePath.substring(indexOfFinalSlash + 1);
        String fileDirectory = filePath.substring(0, indexOfFinalSlash);
        Path directory = getDirectory(fileDirectory);

        try(Stream<Path> fileStream = Files.list(directory)) {

            List<Path> files = fileStream
                    .filter(Files::isRegularFile)
                    .toList();

            for(Path path: files) {
                if (!path.toFile().getName().equals(fileName)) {
                    continue;
                }
                Files.delete(path);
                return;
            }
        } catch (Exception e) {
            throw new FileIOException("Unable to delete file: " + filePath, e);
        }
    }

    /**
     * Creates the directories from the provided directory path. The path must not begin with /
     * @param directoryPath Path of directories to create
     * @throws RuntimeException Thrown if an issue occurs when creating directories
     */
    public static void createDirectory(String directoryPath) throws RuntimeException {
        String[] directories = directoryPath.split("/");
        Path directory = Paths.get(directories[0]);

        for (int i = 1; i < directories.length; i++) {
            directory = directory.resolve(directories[i]);
        }

        try {
            Files.createDirectories(directory);
        } catch (Exception e) {
            throw new FileIOException("Unable to create directory structure!", e);
        }
    }

    private static Path getDirectory(String directory) throws FileIOException {
        Path saveDirectory = Paths.get(directory);

        if (!Files.exists(saveDirectory)) {
            throw new FileIOException("Attempted to access directory that does not exist! Directory: " + directory);
        }

        return saveDirectory;
    }

}
