package com.codetransformer.config;

import java.util.Set;

/**
 * Configuration constants for file processing.
 */
public final class FileProcessingConfig {
    private FileProcessingConfig() {
        throw new AssertionError("Configuration class should not be instantiated");
    }

    /**
     * Maximum number of files to process in a single directory
     */
    public static final int MAX_FILES_PER_DIRECTORY = 100;
    
    /**
     * Maximum depth for directory recursion
     */
    public static final int MAX_DIRECTORY_DEPTH = 5;
    
    /**
     * Maximum total files to process
     */
    public static final int MAX_TOTAL_FILES = 500;

    /**
     * Maximum number of files to process before showing the "too large" warning
     */
    public static final int MAX_FILES_THRESHOLD = 300;

    /**
     * Default maximum file size in characters to prevent memory issues.
     * Files larger than this will be truncated.
     */
    public static final int DEFAULT_MAX_FILE_SIZE = 1_000_000; // 1MB in characters

    /**
     * Directories that should be excluded from processing.
     * These typically contain non-code files, build artifacts, or third-party dependencies.
     */
    public static final Set<String> EXCLUDED_DIRS = Set.of(
        ".git", "node_modules", "__pycache__", "target", "build",
        "dist", "out", "bin", ".idea", ".vscode"
    );

    /**
     * File extensions that are considered code files.
     * Organized by category for better maintainability.
     */
    public static final Set<String> CODE_FILE_EXTENSIONS = Set.of(
        // Common programming languages
        "java", "py", "js", "ts", "cpp", "c", "h", "hpp", "cs", "go", "rs", "rb",
        // Web development
        "php", "scala", "kt", "kts", "groovy", "swift", "m", "mm",
        // Shell and scripts
        "sh", "bash", "ps1", "bat", "cmd",
        // Other programming files
        "r", "pl", "pm", "t", "sql", "lua", "elm", "erl", "ex", "exs"
    );
}
