#!/usr/bin/env python3
"""
Application: Code Transformer

This application processes source code from various programming languages (Java, Python, Node.js, etc.) and structures it in a format optimized for automatic readers.

Features:
- Extracts code from its original files, preserving the directory structure including packages and accompanying files.
- Formats output such that each file is clearly labeled with its relative file path and the content is provided in a structured format.
- Provides a user-friendly GUI for selecting the project directory and initiating the transformation.

Usage:
    Simply run the application, select a directory, specify an output file, and click "Transform".

Example Output for a Java file:

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

The output file will include such details for every source file found in the directory tree.
"""

import os
import sys
import tkinter as tk
from tkinter import filedialog, messagebox

def build_directory_tree(root_dir):
    """
    Recursively builds a directory tree diagram as a string starting from root_dir.
    Excludes Git repository files and directories like .git/objects.
    """
    tree_lines = []
    base = os.path.basename(os.path.normpath(root_dir))
    tree_lines.append(base)

    # Directories and files to exclude from the tree
    excluded_dirs = ['.git', 'node_modules', '__pycache__', '.idea', '.vscode']
    excluded_git_dirs = ['objects', 'hooks', 'logs', 'refs']

    def _build_tree(dir_path, prefix=""):
        try:
            entries = sorted(os.listdir(dir_path))
        except PermissionError:
            return
        
        # Filter out entries that should be excluded
        filtered_entries = []
        for entry in entries:
            # Skip excluded directories
            if entry in excluded_dirs:
                continue
                
            full_path = os.path.join(dir_path, entry)
            
            # Skip Git internal directories
            if os.path.basename(os.path.dirname(full_path)) == '.git' and entry in excluded_git_dirs:
                continue
                
            # Skip Git object files (which are typically named with hex characters)
            if os.path.basename(os.path.dirname(os.path.dirname(full_path))) == 'objects' and len(entry) == 2 and all(c in '0123456789abcdef' for c in entry):
                continue
                
            filtered_entries.append(entry)
            
        entries_count = len(filtered_entries)
        for idx, entry in enumerate(filtered_entries):
            full_path = os.path.join(dir_path, entry)
            is_last = (idx == entries_count - 1)
            if is_last:
                tree_lines.append(prefix + "└── " + entry)
                extension_prefix = prefix + "    "
            else:
                tree_lines.append(prefix + "├── " + entry)
                extension_prefix = prefix + "│   "
            if os.path.isdir(full_path):
                _build_tree(full_path, extension_prefix)
    
    _build_tree(root_dir)
    return "\n".join(tree_lines)


def process_files(source_dir, output_file):
    """
    Recursively processes source files from the source_dir, writes a directory tree diagram, and then writes the file details to output_file.
    """
    # Allowed file extensions for common programming languages
    allowed_extensions = {'.py', '.java', '.js', '.ts', '.jsx', '.tsx', '.c', '.cpp', '.cs', '.rb', '.go', '.php'}

    with open(output_file, 'w', encoding='utf-8') as outfile:
        # Write the directory tree diagram at the top
        tree_diagram = build_directory_tree(source_dir)
        outfile.write("Directory Structure:\n")
        outfile.write(tree_diagram + "\n")
        outfile.write("\n" + "="*80 + "\n\n")
        
        # Directories to exclude when processing files
        excluded_dirs = ['.git', 'node_modules', '__pycache__', '.idea', '.vscode']
        
        # Process files
        output_content = []
        for root, dirs, files in os.walk(source_dir):
            # Skip excluded directories
            dirs[:] = [d for d in dirs if d not in excluded_dirs]
            
            # Sort files for consistency
            files.sort()
            for file in files:
                file_path = os.path.join(root, file)
                _, ext = os.path.splitext(file)
                # Check if file extension is allowed or if it's a file that likely contains source code
                if ext.lower() in allowed_extensions or file.lower() in ['makefile', 'dockerfile']:
                    try:
                        with open(file_path, 'r', encoding='utf-8') as infile:
                            content = infile.read()
                    except Exception as e:
                        content = f"Error reading file: {e}"
                    # Write file header and content to output
                    rel_path = os.path.relpath(file_path, source_dir)
                    file_output = f"File Path: {rel_path}\nContent:\n{content}\n\n{'-'*80}\n\n"
                    output_content.append(file_output)
                    outfile.write(file_output)
        
        # Copy the complete output to clipboard
        complete_output = tree_diagram + "\n\n" + "="*80 + "\n\n" + "".join(output_content)
        root = tk.Tk()
        root.withdraw()  # Hide the root window
        root.clipboard_clear()
        root.clipboard_append(complete_output)
        root.update()  # Required for clipboard to work
        root.destroy()


def run_gui():
    '''Creates the GUI for selecting the project directory and output file, then runs the transformation.'''
    root = tk.Tk()
    root.title("Code Transformer")

    frame = tk.Frame(root, padx=10, pady=10)
    frame.pack()

    source_dir = tk.StringVar()
    output_file = tk.StringVar(value="structured-output.txt")

    def browse_directory():
        directory = filedialog.askdirectory(title="Select Project Directory")
        if directory:
            source_dir.set(directory)

    def transform():
        if not source_dir.get():
            messagebox.showerror("Error", "Please select a source directory.")
            return
        try:
            process_files(source_dir.get(), output_file.get())
            messagebox.showinfo("Success", f"Transformation complete. Output saved to {output_file.get()}")
        except Exception as e:
            messagebox.showerror("Error", str(e))

    tk.Label(frame, text="Source Directory:").grid(row=0, column=0, sticky="w")
    tk.Entry(frame, textvariable=source_dir, width=50).grid(row=0, column=1, padx=5)
    tk.Button(frame, text="Browse", command=browse_directory).grid(row=0, column=2, padx=5)

    tk.Label(frame, text="Output File:").grid(row=1, column=0, sticky="w")
    tk.Entry(frame, textvariable=output_file, width=50).grid(row=1, column=1, padx=5)

    tk.Button(frame, text="Transform", command=transform).grid(row=2, column=1, pady=10)
    tk.Button(frame, text="Quit", command=root.quit).grid(row=2, column=2, pady=10)

    root.mainloop()


def main():
    run_gui()

if __name__ == '__main__':
    main()
