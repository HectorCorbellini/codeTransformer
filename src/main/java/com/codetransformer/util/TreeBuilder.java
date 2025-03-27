package com.codetransformer.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.codetransformer.ui.UIConstants;

/**
 * Utility class for building directory tree representations.
 */
public class TreeBuilder {
    private static final String BASE_DIR_PREFIX = "└── ";
    private static final String EXTENSION_PREFIX = "    ";
    private static final String BRANCH_PREFIX = "├── ";
    private static final String VERTICAL_PREFIX = "│   ";
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private static final Set<String> EXCLUDED_DIRS = UIConstants.EXCLUDED_DIRS;

    private static final Set<String> EXCLUDED_GIT_DIRS = new HashSet<>(Arrays.asList(
        "objects", "hooks", "logs", "refs"
    ));

    /**
     * Builds a directory tree diagram as a string starting from root directory.
     * @param rootDir The root directory to start from
     * @return A string representation of the directory tree
     * @throws IOException if there's an error accessing the filesystem
     */
    public static String buildDirectoryTree(Path rootDir) throws IOException {
        StringBuilder treeLines = new StringBuilder();
        treeLines.append(rootDir.getFileName()).append(LINE_SEPARATOR);
        buildTree(rootDir, "", treeLines);
        return treeLines.toString();
    }

    private static void buildTree(Path dir, String prefix, StringBuilder treeLines) throws IOException {
        List<Path> entries = Files.list(dir)
            .filter(TreeBuilder::shouldInclude)
            .sorted()
            .collect(Collectors.toList());

        for (int i = 0; i < entries.size(); i++) {
            Path entry = entries.get(i);
            boolean isLast = (i == entries.size() - 1);
            
            // Add current entry to tree
            treeLines.append(prefix)
                    .append(isLast ? BASE_DIR_PREFIX : BRANCH_PREFIX)
                    .append(entry.getFileName())
                    .append(LINE_SEPARATOR);

            // Recursively process directory
            if (Files.isDirectory(entry)) {
                buildTree(entry, 
                         prefix + (isLast ? EXTENSION_PREFIX : VERTICAL_PREFIX),
                         treeLines);
            }
        }
    }

    private static boolean shouldInclude(Path path) {
        String name = path.getFileName().toString();
        
        // Skip excluded directories
        if (EXCLUDED_DIRS.contains(name)) {
            return false;
        }
        
        // Skip Git internal directories
        if (name.equals(".git")) {
            return false;
        }
        
        Path parent = path.getParent();
        if (parent != null && parent.getFileName() != null) {
            String parentName = parent.getFileName().toString();
            if (parentName.equals(".git") && EXCLUDED_GIT_DIRS.contains(name)) {
                return false;
            }
            
            // Skip Git object files
            if (parentName.equals("objects") && 
                name.length() == 2 && 
                name.matches("[0-9a-f]{2}")) {
                return false;
            }
        }
        
        return true;
    }
}
