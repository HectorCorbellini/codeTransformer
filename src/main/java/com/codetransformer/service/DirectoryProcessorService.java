package com.codetransformer.service;

import com.codetransformer.model.TransformationResult;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Service interface for processing directories and transforming their contents.
 * Follows Interface Segregation Principle by providing focused methods.
 */
public interface DirectoryProcessorService {
    /**
     * Processes a directory and transforms its contents into a single text file.
     * Only includes code files, skipping documentation and other non-code files.
     * 
     * @param sourceDir Source directory to process
     * @return TransformationResult containing the processed content
     */
    TransformationResult processDirectory(Path sourceDir);

    /**
     * Counts the number of code files in a directory and its subdirectories.
     * This is used to check if a codebase is too large before processing.
     * 
     * @param dir Directory to count files in
     * @param maxFiles Maximum number of files to count before stopping
     * @return Number of code files found (up to maxFiles)
     * @throws IOException if an error occurs during directory traversal
     */
    int countCodeFiles(Path dir, int maxFiles) throws IOException;
} 