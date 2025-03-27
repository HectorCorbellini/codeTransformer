package com.codetransformer.util;

import com.codetransformer.config.FileProcessingConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementation of FileService interface.
 * Following Single Responsibility Principle - handles only file-related operations.
 */
public final class FileUtils implements FileService {
    // Private constructor to prevent instantiation
    private FileUtils() {
        // Private constructor for singleton pattern
    }

    private static final FileUtils INSTANCE = new FileUtils();

    /**
     * Gets the singleton instance of FileUtils.
     * @return The FileUtils instance
     */
    public static FileUtils getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean shouldProcessDirectory(Path path) {
        if (path == null) {
            return false;
        }
        String dirName = path.getFileName().toString().toLowerCase();
        return !FileProcessingConfig.EXCLUDED_DIRS.contains(dirName);
    }

    @Override
    public String readFileContent(Path path) throws IOException {
        return readFileContent(path, FileProcessingConfig.DEFAULT_MAX_FILE_SIZE);
    }
    
    @Override
    public String readFileContent(Path path, int maxSize) throws IOException {
        if (path == null) {
            throw new NullPointerException("File path cannot be null");
        }
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be positive");
        }
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + path);
        }
        if (!Files.isRegularFile(path)) {
            throw new IOException("Path is not a regular file: " + path);
        }
        
        String content = Files.readString(path);
        if (content.length() > maxSize) {
            content = content.substring(0, maxSize) + 
                     "\n... (file truncated due to size limit of " + maxSize + " characters) ...";
        }
        return content;
    }

    @Override
    public void writeFileContent(Path path, String content) throws IOException {
        if (path == null) {
            throw new NullPointerException("File path cannot be null");
        }
        if (content == null) {
            throw new NullPointerException("Content cannot be null");
        }
        Files.writeString(path, content);
    }

    @Override
    public boolean isCodeFile(Path path) {
        if (path == null) {
            throw new NullPointerException("File path cannot be null");
        }
        
        if (Files.isDirectory(path)) {
            return false;
        }
        
        String fileName = path.getFileName().toString().toLowerCase();
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            String extension = fileName.substring(lastDot + 1);
            return FileProcessingConfig.CODE_FILE_EXTENSIONS.contains(extension);
        }
        return false;
    }
}
