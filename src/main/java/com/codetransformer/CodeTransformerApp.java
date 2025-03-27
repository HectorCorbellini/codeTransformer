package com.codetransformer;

import com.codetransformer.service.DirectoryProcessorService;
import com.codetransformer.service.DirectoryProcessorServiceImpl;
import com.codetransformer.ui.MainWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application class.
 * Follows Single Responsibility Principle - only handles application startup.
 */
public class CodeTransformerApp {
    private static final Logger LOGGER = Logger.getLogger(CodeTransformerApp.class.getName());

    /**
     * Application entry point.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for better integration
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                // Fallback to default look and feel
                LOGGER.log(Level.WARNING, "Could not set system look and feel", e);
            }

            // Initialize the directory processor service
            DirectoryProcessorService processor = new DirectoryProcessorServiceImpl();
            
            // Pass the processor to the MainWindow
            new MainWindow(processor).setVisible(true);
        });
    }
}
