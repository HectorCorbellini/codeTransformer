package com.codetransformer.service;

import com.codetransformer.config.FileProcessingConfig;
import com.codetransformer.model.TransformationResult;
import com.codetransformer.util.FileService;
import com.codetransformer.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the DirectoryProcessorService interface.
 * Responsible for processing directories and transforming their contents.
 * This implementation follows the same successful pattern from the reference project.
 */
public class DirectoryProcessorServiceImpl implements DirectoryProcessorService {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String FILE_SEPARATOR = "=".repeat(80) + LINE_SEPARATOR;
    
    // Use the FileService interface for file operations
    private final FileService fileService;
    
    /**
     * Constructs a new DirectoryProcessorServiceImpl instance.
     */
    public DirectoryProcessorServiceImpl() {
        this.fileService = FileUtils.getInstance();
    }
    
    /**
     * Constructs a new DirectoryProcessorServiceImpl with a specified file service.
     * This constructor is primarily for testing purposes, allowing dependency injection.
     * 
     * @param fileService The file service to use
     */
    public DirectoryProcessorServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public TransformationResult processDirectory(Path sourceDir) {
        TransformationResult.Builder resultBuilder = new TransformationResult.Builder();
        
        try {
            validateDirectory(sourceDir);
            String content = processDirectoryContent(sourceDir, 0);
            String outputPath = determineOutputPath(sourceDir);
            
            fileService.writeFileContent(Path.of(outputPath), content);
            
            return resultBuilder
                .withContent(content)
                .withOutputPath(outputPath)
                .withSuccess(true)
                .build();
                
        } catch (IOException e) {
            return resultBuilder
                .withErrorMessage("Error processing directory: " + e.getMessage())
                .withSuccess(false)
                .build();
        }
    }
    
    @Override
    public int countCodeFiles(Path dir, int maxFiles) throws IOException {
        if (!Files.isDirectory(dir)) {
            return 0;
        }
        
        int count = 0;
        try (var paths = Files.walk(dir)) {
            List<Path> files = paths.filter(Files::isRegularFile)
                                    .filter(fileService::isCodeFile)
                                    .limit(maxFiles + 1)
                                    .collect(Collectors.toList());
            
            count = files.size();
        }
        
        return Math.min(count, maxFiles);
    }

    /**
     * Validates that the provided path is a directory.
     * @param dir Path to validate
     * @throws IOException if the path is not a directory
     */
    private void validateDirectory(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }
    }

    /**
     * Determines the output path for the transformed content.
     * @param sourceDir Source directory
     * @return String path for the output file
     */
    private String determineOutputPath(Path sourceDir) {
        return sourceDir.getParent().resolve(sourceDir.getFileName() + "_code_only.txt").toString();
    }

    /**
     * Processes the content of a directory recursively.
     * @param dir Directory to process
     * @param depth Current depth in the directory tree
     * @return String containing the processed content
     * @throws IOException if an error occurs during processing
     */
    private String processDirectoryContent(Path dir, int depth) throws IOException {
        // Skip processing if we've gone too deep
        if (depth > FileProcessingConfig.MAX_DIRECTORY_DEPTH) {
            return "";
        }
        
        StringBuilder content = new StringBuilder();
        String indent = "  ".repeat(depth);

        // Add directory name
        appendDirectoryHeader(content, dir, indent);

        // Process all files and subdirectories
        List<Path> sortedPaths = getSortedDirectoryContents(dir);
        
        for (Path path : sortedPaths) {
            if (isProcessableDirectory(path)) {
                String subDirContent = processDirectoryContent(path, depth + 1);
                // Only add directory if it contains code files
                if (containsCodeFiles(subDirContent)) {
                    content.append(subDirContent);
                }
            } else if (fileService.isCodeFile(path)) {
                content.append(processFile(path, depth + 1));
            }
        }

        return content.toString();
    }
    
    /**
     * Appends the directory header to the content.
     * @param content StringBuilder to append to
     * @param dir Directory path
     * @param indent Current indentation
     */
    private void appendDirectoryHeader(StringBuilder content, Path dir, String indent) {
        content.append(indent)
              .append("[Directory: ")
              .append(dir.getFileName())
              .append("]")
              .append(LINE_SEPARATOR);
    }
    
    /**
     * Gets a sorted list of paths in the directory.
     * @param dir Directory to list
     * @return Sorted list of paths
     * @throws IOException if an error occurs listing the directory
     */
    private List<Path> getSortedDirectoryContents(Path dir) throws IOException {
        try (var stream = Files.list(dir).sorted()) {
            return stream.collect(Collectors.toList());
        }
    }
    
    /**
     * Checks if a directory should be processed.
     * @param path Path to check
     * @return true if the directory should be processed
     */
    private boolean isProcessableDirectory(Path path) {
        return Files.isDirectory(path) && fileService.shouldProcessDirectory(path);
    }
    
    /**
     * Checks if the content contains code files.
     * @param content Content to check
     * @return true if the content contains code files
     */
    private boolean containsCodeFiles(String content) {
        return content.contains("[File:");
    }

    /**
     * Processes a single file.
     * @param file File to process
     * @param depth Current depth in the directory tree
     * @return String containing the processed file content
     * @throws IOException if an error occurs reading the file
     */
    private String processFile(Path file, int depth) throws IOException {
        StringBuilder content = new StringBuilder();
        String indent = "  ".repeat(depth);
        
        content.append(indent)
              .append("[File: ")
              .append(file.getFileName())
              .append("]")
              .append(LINE_SEPARATOR)
              .append(FILE_SEPARATOR)
              .append(fileService.readFileContent(file))
              .append(LINE_SEPARATOR)
              .append(FILE_SEPARATOR);

        return content.toString();
    }
}
