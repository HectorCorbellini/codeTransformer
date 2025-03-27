package com.codetransformer.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for generating AI platform URLs with code content.
 * Follows Single Responsibility Principle by handling only URL generation.
 */
public final class AIPlatformURLs {
    private static final Logger LOGGER = Logger.getLogger(AIPlatformURLs.class.getName());
    // Private constructor to prevent instantiation
    private AIPlatformURLs() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    private static final String CHATGPT_URL = "https://chatgpt.com/g/g-cksUvVWar-code-gpt-python-java-c-html-javascript-more";
    private static final String GEMINI_URL = "https://gemini.google.com/app?hl=es-MX";
    private static final String COPILOT_URL = "https://copilot.microsoft.com/chats/w6FbF6vEyQRbvKnVDC9CU";
    private static final String CODEIUM_URL = "https://codeium.com/live/general";
    
    private static final String[] SUPPORTED_PLATFORMS = {"ChatGPT", "Gemini", "Copilot", "Codeium"};

    /**
     * Generate a URL for the specified AI platform with code content.
     * @param platform The AI platform ("chatgpt", "gemini", "copilot", "codeium")
     * @param codeContent The code content to analyze
     * @return A URI for the AI platform with the code content
     * @throws IllegalArgumentException if the platform is not supported
     * @throws RuntimeException if there is an error creating the URI
     */
    public static URI generateAIURL(String platform, String codeContent) {
        try {
            URI result;
            
            switch (platform.toLowerCase()) {
                case "chatgpt":
                    result = new URI(CHATGPT_URL);
                    break;
                case "gemini":
                    result = new URI(GEMINI_URL);
                    break;
                case "copilot":
                    result = new URI(COPILOT_URL);
                    break;
                case "codeium":
                    result = new URI(CODEIUM_URL);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported AI platform: " + platform);
            }
            
            return result;
        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "Invalid URL for AI platform: " + platform, e);
            throw new IllegalArgumentException("Invalid URL for AI platform: " + platform, e);
        }
    }

    /**
     * Get a list of supported AI platforms.
     * @return An array of supported platform names
     */
    public static String[] getSupportedPlatforms() {
        return SUPPORTED_PLATFORMS.clone();
    }
}
