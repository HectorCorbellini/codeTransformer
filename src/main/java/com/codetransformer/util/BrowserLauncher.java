package com.codetransformer.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for launching web browsers across different platforms.
 * Provides fallback mechanisms when the standard Desktop API is not available.
 */
public class BrowserLauncher {
    
    private static final Logger LOGGER = Logger.getLogger(BrowserLauncher.class.getName());
    
    /**
     * Attempts to open a URL in a browser using multiple methods.
     * 
     * @param url The URL to open
     * @return A result object containing success status and any error message
     */
    public static BrowserLaunchResult openURL(URI url) {
        // Try using the Desktop API first (most reliable when available)
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(url);
                return new BrowserLaunchResult(true, null);
            } catch (IOException e) {
                // Fall through to alternative methods
            }
        }
        
        // Get the operating system
        String os = System.getProperty("os.name").toLowerCase();
        
        // Try to launch browser using OS-specific commands
        try {
            if (os.contains("win")) {
                // Windows
                return runCommand("rundll32", "url.dll,FileProtocolHandler", url.toString());
            } else if (os.contains("mac")) {
                // macOS
                return runCommand("open", url.toString());
            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux/Unix - try different browsers in sequence
                BrowserLaunchResult result = runCommand("xdg-open", url.toString());
                if (result.isSuccess()) return result;
                
                // Try common browsers
                String[] browsers = {
                    "google-chrome", "firefox", "mozilla", "epiphany", "konqueror", 
                    "netscape", "opera", "links", "lynx"
                };
                
                for (String browser : browsers) {
                    result = runCommand(browser, url.toString());
                    if (result.isSuccess()) return result;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error launching browser", e);
            return new BrowserLaunchResult(false, "Error launching browser: " + e.getMessage());
        }
        
        return new BrowserLaunchResult(false, "Could not find a browser to launch on this system");
    }
    
    /**
     * Runs a system command with arguments.
     * 
     * @param command The command to run
     * @param args The arguments for the command
     * @return A result object containing success status and any error message
     */
    private static BrowserLaunchResult runCommand(String command, String... args) {
        try {
            List<String> cmdList = new ArrayList<>();
            cmdList.add(command);
            cmdList.addAll(Arrays.asList(args));
            
            Process process = new ProcessBuilder(cmdList).start();
            
            // Wait a bit to see if the process crashes immediately
            try {
                Thread.sleep(300);
                int exitValue = process.exitValue();
                if (exitValue != 0) {
                    return new BrowserLaunchResult(false, "Command exited with code: " + exitValue);
                }
            } catch (IllegalThreadStateException e) {
                // Process is still running, which is good
                return new BrowserLaunchResult(true, null);
            }
            
            return new BrowserLaunchResult(true, null);
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.WARNING, "Error running browser command", e);
            return new BrowserLaunchResult(false, e.getMessage());
        }
    }
    
    /**
     * Result class for browser launch attempts.
     */
    public static class BrowserLaunchResult {
        private final boolean success;
        private final String errorMessage;
        
        public BrowserLaunchResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
