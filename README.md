# Code Transformer

## Overview
Code Transformer is a Java application designed to combine multiple code files from a directory into a single text file. This makes it easier to share codebases with AI tools like ChatGPT, Gemini, GitHub Copilot, and Codeium for analysis and interpretation.

![Code Transformer Screenshot](https://i.imgur.com/example.png)

## Features

### Core Functionality
- **Directory Processing**: Recursively processes all code files in a selected directory
- **Intelligent Filtering**: Automatically excludes non-code files (binaries, images, etc.)
- **Structured Output**: Preserves file paths and hierarchical structure in the output
- **Drag & Drop Support**: Easily select directories by dragging them into the application
- **Clipboard Integration**: Copy the generated code directly to clipboard

### AI Platform Integration
- **One-Click AI Access**: Dedicated buttons for popular AI platforms (ChatGPT, Gemini, Copilot, Codeium)
- **Automatic Clipboard Copy**: When opening an AI platform, code is automatically copied to clipboard for easy pasting
- **Browser Integration**: Opens the selected AI platform in the default web browser
- **Fallback Mechanisms**: Provides clickable links if the browser cannot be launched automatically

### User Experience
- **Modern UI**: Clean, intuitive interface with responsive design
- **Progress Tracking**: Visual progress indicators during processing
- **Error Handling**: Comprehensive error messages and recovery options
- **Comprehensive Help**: Detailed help dialog with usage instructions and tips

## Technical Architecture

### Package Structure
- `com.codetransformer`: Main package
  - `.model`: Data models and transformation result classes
  - `.service`: Core business logic for directory processing
  - `.ui`: User interface components
  - `.util`: Utility classes for browser launching, file operations, etc.

### Key Components

#### MainWindow
The primary UI class that handles user interactions and displays results. It manages:
- Directory selection and validation
- Code transformation process
- Results display and clipboard operations
- AI platform integration

#### DirectoryProcessorService
Interface and implementation that handle the recursive processing of directories, including:
- File type detection and filtering
- Content extraction
- Output formatting
- Error handling and validation

#### AIPlatformURLs
Utility class for generating URLs for various AI platforms.

#### BrowserLauncher
Cross-platform browser launching utility with fallback mechanisms.

## Development Guide

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Maven 3.6.0 or higher

### Building the Project
```bash
# Clone the repository
git clone https://github.com/yourusername/code-transformer.git
cd code-transformer

# Build with Maven
mvn clean package
```

The build process will generate two JAR files in the `target` directory:
- `code-transformer-1.0-SNAPSHOT.jar`: The basic JAR file
- `code-transformer-1.0-SNAPSHOT-jar-with-dependencies.jar`: A standalone executable JAR with all dependencies included

### Running the Application
```bash
java -jar target/code-transformer-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── codetransformer/
│   │           ├── CodeTransformerApp.java  # Main application entry point
│   │           ├── model/
│   │           │   └── TransformationResult.java
│   │           ├── service/
│   │           │   ├── DirectoryProcessorService.java
│   │           │   └── DirectoryProcessorServiceImpl.java
│   │           ├── ui/
│   │           │   └── MainWindow.java
│   │           └── util/
│   │               ├── AIPlatformURLs.java
│   │               ├── BrowserLauncher.java
│   │               └── FileUtils.java
│   └── resources/
└── test/
    └── java/
        └── com/
            └── codetransformer/
                └── AppTest.java
```

## Implementation Details

### File Processing
The application uses a service-based approach with clear separation of concerns:
1. The DirectoryProcessorService interface defines the contract for directory processing
2. DirectoryProcessorServiceImpl implements the processing logic:
   - Validates input directories
   - Recursively scans for code files
   - Filters based on extensions and content type
   - Reads and formats each file's content
   - Combines content with appropriate headers and separators
   - Handles errors and provides detailed feedback

### Browser Integration
The application uses a multi-layered approach for browser launching:
1. First attempts to use Java's Desktop API
2. Falls back to OS-specific commands if Desktop API is unavailable
3. Provides clickable links as a final fallback option

### Clipboard Handling
To ensure reliable clipboard operations, especially when interacting with browsers:
1. The application uses a delayed clipboard operation after browser launch
2. This prevents the browser from overwriting the clipboard content
3. A confirmation message is shown when the clipboard operation is successful

## Extending the Application

### Adding Support for New File Types
To add support for new file types, modify the `FileUtils` class:

```java
// Example: Adding support for a new language
public static final Set<String> CODE_EXTENSIONS = new HashSet<>(Arrays.asList(
    // Existing extensions...
    "newlang"  // Add your new extension here
));
```

### Adding New AI Platforms
To add support for a new AI platform:

1. Update the `AIPlatformURLs` class:
```java
// Add a new constant for the platform URL
private static final String NEW_PLATFORM_URL = "https://new-platform.com/path";

// Update the generateAIURL method to include the new platform
case "newplatform":
    result = new URI(NEW_PLATFORM_URL);
    break;

// Update the getSupportedPlatforms method
public static String[] getSupportedPlatforms() {
    return new String[]{"ChatGPT", "Gemini", "Copilot", "Codeium", "NewPlatform"};
}
```

2. Update the UI in `MainWindow` to add a new button for the platform

## Troubleshooting

### Common Issues

#### Application Won't Start
- Ensure you have Java 11 or higher installed
- Check that you're using the JAR with dependencies
- Verify file permissions

#### Browser Launch Failures
- Check your default browser settings
- Ensure you have internet connectivity
- Try using the clipboard option and manually opening the AI platform

#### Large Directory Processing
- For very large directories, the application may require additional memory
- Run with increased heap size: `java -Xmx2g -jar code-transformer-1.0-SNAPSHOT-jar-with-dependencies.jar`

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Contributors
- Original Developer: [Your Name]
- Contributors: [List of contributors]

## Acknowledgments
- Thanks to all the AI platforms that inspired this integration tool
- Special thanks to the Java Swing community for UI component inspiration
