package SACS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Resolves the application storage directory without assuming a platform path.
 */
public final class Storage {

    private static final String SETTING = "SACS_STORAGE_DIR";

    private Storage() {
    }

    public static File root() throws IOException {
        String configured = System.getProperty(SETTING);
        if (configured == null || configured.trim().isEmpty()) {
            configured = System.getenv(SETTING);
        }
        String fallback = System.getProperty("java.io.tmpdir");
        if (fallback == null || fallback.trim().isEmpty()) {
            fallback = System.getProperty("user.home", ".");
        }
        Path root = configured == null || configured.trim().isEmpty()
                ? Paths.get(fallback, "sacs-storage") : Paths.get(configured);
        root = root.toAbsolutePath().normalize();
        File directory = root.toFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Unable to create storage directory " + directory);
        }
        if (!directory.isDirectory()) {
            throw new IOException("Storage path is not a directory: " + directory);
        }
        return directory;
    }

    public static File directory(String... parts) throws IOException {
        Path root = root().toPath();
        Path path = root;
        for (String part : parts) {
            if (part == null || part.isEmpty() || part.indexOf('/') >= 0 || part.indexOf('\\') >= 0
                    || ".".equals(part) || "..".equals(part)) {
                throw new IOException("Invalid storage path component");
            }
            path = path.resolve(part);
        }
        path = path.normalize();
        if (!path.startsWith(root)) {
            throw new IOException("Storage path escapes configured directory");
        }
        File directory = path.toFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Unable to create storage directory " + directory);
        }
        if (!directory.isDirectory()) {
            throw new IOException("Storage path is not a directory: " + directory);
        }
        return directory;
    }

    public static File file(File directory, String name) throws IOException {
        if (directory == null || name == null || name.isEmpty()
                || name.indexOf('/') >= 0 || name.indexOf('\\') >= 0
                || ".".equals(name) || "..".equals(name)) {
            throw new IOException("Invalid storage file name");
        }
        File file = new File(directory, name).getCanonicalFile();
        File base = directory.getCanonicalFile();
        if (!file.toPath().startsWith(base.toPath())) {
            throw new IOException("Storage file escapes configured directory");
        }
        return file;
    }
}
