package com.codetransformer.service;

import com.codetransformer.model.TransformationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DirectoryProcessorService.
 * Demonstrates proper unit testing practices.
 */
class DirectoryProcessorTest {
    private DirectoryProcessorService processor;
    
    @BeforeEach
    void setUp() {
        processor = new DirectoryProcessorServiceImpl();
    }

    @Test
    void processDirectory_WithCodeFiles_ShouldIncludeOnlyCode(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path javaFile = tempDir.resolve("Test.java");
        Files.writeString(javaFile, "public class Test {}");
        
        Path mdFile = tempDir.resolve("README.md");
        Files.writeString(mdFile, "# Documentation");
        
        Path pyFile = tempDir.resolve("script.py");
        Files.writeString(pyFile, "def main(): pass");

        // Act
        TransformationResult result = processor.processDirectory(tempDir);

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(result.getContent().contains("Test.java"));
        assertTrue(result.getContent().contains("script.py"));
        assertFalse(result.getContent().contains("README.md"));
        assertTrue(Files.exists(Path.of(result.getOutputPath())));
    }

    @Test
    void processDirectory_WithInvalidPath_ShouldFail() {
        // Arrange
        Path invalidPath = Path.of("/nonexistent/path");

        // Act
        TransformationResult result = processor.processDirectory(invalidPath);

        // Assert
        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("Error processing directory"));
    }

    @Test
    void processDirectory_WithEmptyDirectory_ShouldSucceed(@TempDir Path tempDir) throws Exception {
        // Act
        TransformationResult result = processor.processDirectory(tempDir);

        // Assert
        assertTrue(result.isSuccess());
        assertFalse(result.getContent().contains("[File:"));
    }
}
