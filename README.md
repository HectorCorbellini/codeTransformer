# Code Transformer

The Code Transformer application processes source code from various programming languages (e.g., Java, Python, Node.js, etc.) and outputs a structured text file that preserves the directory hierarchy and source code details.

## What It Does

- **Extracts Code:** Recursively reads source files (with extensions like `.py`, `.java`, `.js`, etc.) from a specified directory.
- **Preserves Directory Structure:** Maintains the relative file paths so that the structure of packages and directories is preserved.
- **Formats Output:** Each source file's content is preceded by a header with its relative file path and a clear label. This makes it easier for AI models and human readers to interpret the code structure.
- **Handles Multiple Languages:** Supports a variety of languages by filtering based on file extensions and common filenames (like `Makefile` and `Dockerfile`).
- **Graphical User Interface:** Provides a Tkinter-based GUI for selecting the project directory and specifying the output file. Users can click to transform the code into a structured text file.

## How to Use

1. **Run the Application:**

   ```bash
   python3 code_transformer.py
   ```

2. **In the GUI:**
   - Click **Browse** to select the project directory containing the source code.
   - Specify the output file name (default is "structured_output.txt").
   - Click **Transform** to process the source files and generate the output file.
   - Click **Quit** to exit the application.

## Example Output (Java File)

```
File Path: src/main/java/Location.java
Content:

package com.securitas.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Location {
    private Long id;
    private String name;
    private String type;
    private String address;
    private LocalDateTime createdAt;
}
```

This structured format is designed to be easily read by both humans and AI models, with clear delineation of the directory structure and file content.

## Prerequisites

Ensure that Python3 and Tkinter (python3-tk) are installed on your system.

## License

This project is released under the MIT License.
