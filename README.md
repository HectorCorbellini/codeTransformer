# Code Transformer

The Code Transformer application processes source code from various programming languages (e.g., Java, Python, Node.js, etc.) and outputs a structured text file that preserves the directory hierarchy and source code details. 

## What It Does

- **Extracts Code:** Recursively reads source files (with extensions like `.py`, `.java`, `.js`, etc.) from a specified directory.
- **Preserves Directory Structure:** Maintains the relative file paths so that the structure of packages and directories is preserved.
- **Formats Output:** Each source file's content is preceded by a header with its relative file path and a clear label. This makes it easier for AI models and human readers to interpret the code structure.
- **Handles Multiple Languages:** Supports a variety of languages by filtering based on file extensions and common filenames (like `Makefile` and `Dockerfile`).

## How to Use

1. **Run the Application:**
   ```bash
   python3 code_transformer.py [source_directory] [output_file]
   ```
   - If `source_directory` is not provided, the current directory (`.`) is used.
   - If `output_file` is not provided, `structured_output.txt` is created by default.

2. **View the Output:**
   The generated output file contains all the extracted and formatted source code information.

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

## License

This project is released under the MIT License.
