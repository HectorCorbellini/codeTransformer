package com.codetransformer.util;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Service interface for file operations.
 * Follows Interface Segregation Principle by providing focused file operation methods.
 */
public interface FileService {
    /**
     * Checks if a directory should be processed.
     * Excluded directories are skipped to avoid processing non-code files.
     * 
     * @param path Directory path to check
     * @return true if the directory should be processed
     */
    boolean shouldProcessDirectory(Path path);

    /**
     * Reads file content with proper encoding.
     * Uses the default charset for reading the file.
     * 
     * @param path Path to the file
     * @return Content of the file as string
     * @throws IOException if reading fails
     * @throws NullPointerException if path is null
     */
    String readFileContent(Path path) throws IOException;

    /**
     * Reads file content with proper encoding and size limit.
     * Uses the default charset for reading the file.
     * 
     * @param path Path to the file
     * @param maxSize Maximum number of characters to read
     * @return Content of the file as string, truncated if necessary
     * @throws IOException if reading fails
     * @throws NullPointerException if path is null
     */
    String readFileContent(Path path, int maxSize) throws IOException;

    /**
     * Writes content to a file.
     * 
     * @param path Path to write to
     * @param content Content to write
     * @throws IOException if writing fails
     * @throws NullPointerException if path or content is null
     */
    void writeFileContent(Path path, String content) throws IOException;

    /**
     * Checks if a file is a code file based on its extension.
     * 
     * @param path Path to check
     * @return true if the file is a code file
     * @throws NullPointerException if path is null
     */
    boolean isCodeFile(Path path);
} 