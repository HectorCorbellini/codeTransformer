package com.codetransformer.service;

import com.codetransformer.config.FileProcessingConfig;
import com.codetransformer.model.TransformationResult;
import com.codetransformer.util.FileService;
import com.codetransformer.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implementation of DirectoryProcessorService.
 * Follows Single Responsibility Principle and maintains high cohesion.
 */
public class DirectoryProcessor implements DirectoryProcessorService {
    private static final Logger LOGGER = Logger.getLogger(DirectoryProcessor.class.getName());
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String FILE_SEPARATOR = "=".repeat(80) + LINE_SEPARATOR;
    
    // Counter for total files processed
    // File service dependency
    private final FileService fileService;
    
    // Processing statistics
    private static class ProcessingStats {
        private int totalFilesProcessed;
        private final Path sourceDir;
        
        ProcessingStats(Path sourceDir) {
            this.sourceDir = sourceDir;
            this.totalFilesProcessed = 0;
        }
        
        void incrementFilesProcessed() {
            totalFilesProcessed++;
        }
        
        int getTotalFilesProcessed() {
            return totalFilesProcessed;
        }
        
        Path getSourceDir() {
            return sourceDir;
        }
    }

    /**
     * Creates a new DirectoryProcessor with the default file service.
     */
    public DirectoryProcessor() {
        this(FileUtils.getInstance());
    }

    /**
     * Creates a new DirectoryProcessor with the specified file service.
     * 
     * @param fileService The file service to use
     */
    public DirectoryProcessor(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public TransformationResult processDirectory(Path sourceDir) {
        TransformationResult.Builder resultBuilder = new TransformationResult.Builder();
        
        try {
            validateDirectory(sourceDir);
            ProcessingStats stats = new ProcessingStats(sourceDir);
            String content = processDirectoryContent(sourceDir, 0, stats);
            
            if (!containsCodeFiles(content)) {
                return resultBuilder
                    .withSuccess(false)
                    .withErrorMessage("No code files found in directory")
                    .build();
            }
            
            // Add processing summary
            content = addProcessingSummary(content, stats);
            
            String outputPath = determineOutputPath(sourceDir);
            fileService.writeFileContent(Path.of(outputPath), content);
            
            return resultBuilder
                .withContent(content)
                .withOutputPath(outputPath)
                .withSuccess(true)
                .build();
                
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error processing directory: " + sourceDir, e);
            return resultBuilder
                .withErrorMessage("Error processing directory: " + e.getMessage())
                .withSuccess(false)
                .build();
        }
    }

    /**
     * Validates that the provided path is a directory.
     * @param dir Path to validate
     * @throws IOException if the path is not a directory
     */
    private void validateDirectory(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            LOGGER.log(Level.WARNING, "Path is not a directory: {0}", dir);
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
    private String processDirectoryContent(Path dir, int depth, ProcessingStats stats) throws IOException {
        StringBuilder content = new StringBuilder();
        String indent = "  ".repeat(depth);

        // Add directory name
        appendDirectoryHeader(content, dir, indent);

        // Process all files and subdirectories
        List<Path> sortedPaths = getSortedDirectoryContents(dir);
        
        for (Path path : sortedPaths) {
            if (isProcessableDirectory(path)) {
                if (depth < FileProcessingConfig.MAX_DIRECTORY_DEPTH) {
                    String subDirContent = processDirectoryContent(path, depth + 1, stats);
                    // Only add directory if it contains code files
                    if (containsCodeFiles(subDirContent)) {
                        content.append(subDirContent);
                    }
                }
            } else if (fileService.isCodeFile(path)) {
                if (stats.getTotalFilesProcessed() < FileProcessingConfig.MAX_TOTAL_FILES) {
                    content.append(processFile(path, depth + 1));
                    stats.incrementFilesProcessed();
                }
            }
            if (sortedPaths.indexOf(path) >= FileProcessingConfig.MAX_FILES_PER_DIRECTORY - 1) {
                break;
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
    
    /**
     * Adds a summary of the processing to the content.
     * 
     * @param content The processed content
     * @param sourceDir The source directory
     * @return The content with summary added
     */
    private String addProcessingSummary(String content, ProcessingStats stats) {
        StringBuilder summary = new StringBuilder();
        summary.append("=".repeat(80)).append(LINE_SEPARATOR);
        summary.append("CODE TRANSFORMATION SUMMARY").append(LINE_SEPARATOR);
        summary.append("=".repeat(80)).append(LINE_SEPARATOR);
        summary.append("Source Directory: ").append(stats.getSourceDir()).append(LINE_SEPARATOR);
        summary.append("Files Processed: ").append(stats.getTotalFilesProcessed()).append(LINE_SEPARATOR);
        
        if (stats.getTotalFilesProcessed() >= FileProcessingConfig.MAX_TOTAL_FILES) {
            summary.append("NOTE: File limit reached (").append(FileProcessingConfig.MAX_TOTAL_FILES)
                   .append(" files). Some files may have been skipped.").append(LINE_SEPARATOR);
        }
        
        summary.append("=".repeat(80)).append(LINE_SEPARATOR);
        summary.append(LINE_SEPARATOR).append(LINE_SEPARATOR);
        
        return summary.toString() + content;
    }

    @Override
    public int countCodeFiles(Path dir, int maxFiles) throws IOException {
        if (!Files.isDirectory(dir)) {
            return 0;
        }
        
        int count = 0;
        try (var stream = Files.list(dir).sorted()) {
            for (Path path : stream.collect(Collectors.toList())) {
                if (Files.isDirectory(path) && fileService.shouldProcessDirectory(path)) {
                    count += countCodeFiles(path, maxFiles - count);
                } else if (fileService.isCodeFile(path)) {
                    count++;
                }
                
                if (count >= maxFiles) {
                    break;
                }
            }
        }
        
        return count;
    }
}
