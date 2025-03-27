package com.codetransformer.ui;

import com.codetransformer.config.FileProcessingConfig;
import com.codetransformer.model.TransformationResult;
import com.codetransformer.service.DirectoryProcessorService;
import com.codetransformer.util.AIPlatformURLs;
import com.codetransformer.util.BrowserLauncher;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import javax.swing.Timer;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ExecutionException;

/**
 * Main window of the application.
 * Follows Single Responsibility Principle by handling only UI concerns.
 * Implements the user interface for the Code Transformer application.
 */
public class MainWindow extends JFrame {
    // Logo path is defined in UIConstants
    private BufferedImage logoImage;
    

    
    // UI Components
    private JTextField directoryField;
    private JTextPane statusArea;
    private JButton transformButton;
    private JButton copyButton;
    private JButton helpButton;
    private JButton aiAnalysisButton;
    
    // Service dependencies
    private DirectoryProcessorService directoryProcessor;
    
    /**
     * Creates a new MainWindow with the specified directory processor.
     * 
     * @param directoryProcessor The directory processor to use
     */
    public MainWindow(DirectoryProcessorService directoryProcessor) {
        super("Code Transformer");
        this.directoryProcessor = directoryProcessor;
        loadLogo();
        initializeUI();
        setupDropTarget();
    }
    
    /**
     * Initializes the user interface components.
     * Sets up the main layout and adds all UI elements.
     */
    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500); 
        setLocationRelativeTo(null);
        setBackground(UIConstants.BACKGROUND_COLOR);

        // Create main panel with border layout
        JPanel mainPanel = createMainPanel();
        add(mainPanel);
        
        updateButtonStates();
    }
    
    /**
     * Creates the main panel with all UI components.
     * 
     * @return The configured main panel
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add artistic title
        JPanel titlePanel = createArtisticTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Create center panel
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    /**
     * Creates the center panel containing input and status areas.
     * 
     * @return The configured center panel
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 15));
        centerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Create input panel
        JPanel inputPanel = createInputPanel();
        centerPanel.add(inputPanel, BorderLayout.NORTH);

        // Create status area with reduced height
        JPanel statusPanel = createStatusPanel();
        
        // Set preferred size to 2/3 of original height
        Dimension statusSize = statusPanel.getPreferredSize();
        statusSize.height = (int)(statusSize.height * 0.67);
        statusPanel.setPreferredSize(statusSize);
        
        centerPanel.add(statusPanel, BorderLayout.CENTER);
        
        return centerPanel;
    }

    /**
     * Creates an artistic title panel with gradient colors and shadow effect.
     * 
     * @return The configured title panel
     */
    private JPanel createArtisticTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                int w = getWidth();
                int h = getHeight();
                
                // Enable antialiasing for smoother rendering
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, UIConstants.TITLE_COLOR_1,
                    w, h, UIConstants.TITLE_COLOR_2
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, w, h, 15, 15);
                
                // Add decorative elements
                g2d.setColor(UIConstants.TITLE_COLOR_3);
                g2d.fillRoundRect(10, h - 15, w - 20, 8, 5, 5);
                
                g2d.setColor(UIConstants.TITLE_COLOR_4);
                g2d.fillRoundRect(10, 7, w - 20, 8, 5, 5);
                
                // Draw text with shadow
                String title = "Code Transformer";
                Font titleFont = new Font("Arial", Font.BOLD, 36);
                g2d.setFont(titleFont);
                
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D textBounds = fm.getStringBounds(title, g2d);
                
                int textX = (w - (int) textBounds.getWidth()) / 2 + 30; // Adjusted for logo
                int textY = (h + fm.getAscent() - fm.getDescent()) / 2;
                
                // Draw shadow
                g2d.setColor(UIConstants.TITLE_SHADOW_COLOR);
                g2d.drawString(title, textX + 2, textY + 2);
                
                // Draw text with gradient - lighter green to white
                GradientPaint textGradient = new GradientPaint(
                    textX, textY - fm.getAscent(), Color.WHITE,
                    textX, textY, new Color(220, 255, 220)
                );
                g2d.setPaint(textGradient);
                g2d.drawString(title, textX, textY);
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 80);
            }
        };
        
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Create logo panel
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                try {
                    // Enable antialiasing
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Create a circular clip
                    int size = Math.min(getWidth(), getHeight());
                    Shape circle = new Ellipse2D.Float(0, 0, size, size);
                    g2d.setClip(circle);
                    
                    if (logoImage != null) {
                        // Draw the image
                        g2d.drawImage(logoImage, 0, 0, size, size, null);
                        
                        // Draw a subtle border
                        g2d.setColor(new Color(0, 0, 0, 50));
                        g2d.setStroke(new BasicStroke(2f));
                        g2d.draw(circle);
                    } else {
                        // Draw placeholder
                        g2d.setColor(Color.WHITE);
                        g2d.fillOval(0, 0, size, size);
                        g2d.setColor(Color.GRAY);
                        g2d.drawOval(0, 0, size, size);
                    }
                } finally {
                    g2d.dispose();
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(60, 60);
            }
        };
        
        logoPanel.setOpaque(false);
        
        // Create a container for the logo with padding
        JPanel logoContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        logoContainer.setOpaque(false);
        logoContainer.add(logoPanel);
        
        // Add logo container to the left side
        panel.add(logoContainer, BorderLayout.WEST);
        
        return panel;
    }

    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

    private void loadLogo() {
        try {
            // Load the image from resources
            logoImage = ImageIO.read(getClass().getResourceAsStream(UIConstants.LOGO_PATH));
            if (logoImage == null) {
                LOGGER.warning("Failed to load logo: Image is null");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading logo", e);
        }
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Create header
        JLabel headerLabel = new JLabel("Select Source Directory");
        headerLabel.setFont(UIConstants.HEADER_FONT);
        headerLabel.setForeground(UIConstants.TEXT_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);

        // Create input field panel
        JPanel inputFieldPanel = createInputFieldPanel();
        panel.add(inputFieldPanel, BorderLayout.CENTER);

        return panel;
    }
    
    /**
     * Creates the input field panel with directory field and browse button.
     * 
     * @return The configured input field panel
     */
    private JPanel createInputFieldPanel() {
        JPanel inputFieldPanel = new JPanel(new BorderLayout(5, 0));
        inputFieldPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        directoryField = createDirectoryField();
        setupDirectoryFieldDropTarget();
        
        JButton browseButton = createStyledButton("Browse");
        browseButton.addActionListener(e -> browseDirectory());
        
        inputFieldPanel.add(directoryField, BorderLayout.CENTER);
        inputFieldPanel.add(browseButton, BorderLayout.EAST);
        
        return inputFieldPanel;
    }
    
    /**
     * Creates the directory text field with appropriate styling.
     * 
     * @return The configured directory field
     */
    private JTextField createDirectoryField() {
        JTextField field = new JTextField("Drag and drop a folder here");
        field.setFont(UIConstants.NORMAL_FONT);
        field.setEditable(false);
        field.setBackground(Color.WHITE);
        field.setBorder(new CompoundBorder(
            new LineBorder(UIConstants.PRIMARY_COLOR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        field.setForeground(Color.GRAY);
        return field;
    }
    
    /**
     * Sets up the drop target for the directory field.
     * Allows users to drag and drop folders into the field.
     */
    private void setupDirectoryFieldDropTarget() {
        new DropTarget(directoryField, new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                if (isDragAcceptable(dtde)) {
                    directoryField.setBorder(new CompoundBorder(
                        new LineBorder(UIConstants.SUCCESS_COLOR, 2),
                        new EmptyBorder(7, 9, 7, 9)
                    ));
                }
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
                directoryField.setBorder(new CompoundBorder(
                    new LineBorder(UIConstants.PRIMARY_COLOR, 1),
                    new EmptyBorder(8, 10, 8, 10)
                ));
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked") // Suppress unchecked cast warning
                    List<File> droppedFiles = (List<File>) dtde.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    
                    processDroppedFiles(droppedFiles);
                    
                    dtde.dropComplete(true);
                } catch (Exception e) {
                    dtde.rejectDrop();
                } finally {
                    resetDirectoryFieldBorder();
                }
            }
        });
    }
    
    /**
     * Processes the files dropped onto the directory field.
     * 
     * @param droppedFiles List of dropped files
     */
    private void processDroppedFiles(List<File> droppedFiles) {
        if (!droppedFiles.isEmpty()) {
            File file = droppedFiles.get(0);
            if (file.isDirectory()) {
                directoryField.setText(file.getAbsolutePath());
                directoryField.setForeground(UIConstants.TEXT_COLOR);
                updateButtonStates();
            } else {
                showError("Please drop a folder, not a file.");
            }
        }
    }
    
    /**
     * Resets the directory field border to its default state.
     */
    private void resetDirectoryFieldBorder() {
        directoryField.setBorder(new CompoundBorder(
            new LineBorder(UIConstants.PRIMARY_COLOR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
    }

    /**
     * Creates the status panel containing the status text area.
     * 
     * @return The configured status panel
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel statusLabel = new JLabel("Processing Status");
        statusLabel.setFont(UIConstants.HEADER_FONT);
        statusLabel.setForeground(UIConstants.TEXT_COLOR);
        panel.add(statusLabel, BorderLayout.NORTH);

        statusArea = createStatusTextPane();
        JScrollPane scrollPane = createStatusScrollPane(statusArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    /**
     * Creates the status text pane with appropriate styling.
     * 
     * @return The configured status text pane
     */
    private JTextPane createStatusTextPane() {
        JTextPane area = new JTextPane();
        area.setEditable(false);
        area.setFont(UIConstants.NORMAL_FONT);
        area.setContentType("text/html");
        area.setBorder(new CompoundBorder(
            new LineBorder(UIConstants.PRIMARY_COLOR.brighter(), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
        area.setBackground(Color.WHITE);
        
        // Add hyperlink listener to handle clicks on links
        area.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                    showInfo("Opening link in your browser...");
                } catch (Exception ex) {
                    showError("Failed to open link: " + ex.getMessage());
                }
            } else if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                area.setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
                area.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return area;
    }
    
    /**
     * Creates a scroll pane for the status text area.
     * 
     * @param textArea The text area to wrap in a scroll pane
     * @return The configured scroll pane
     */
    private JScrollPane createStatusScrollPane(JTextPane textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Set preferred size for the scroll pane to reduce height
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 150));
        
        return scrollPane;
    }

    /**
     * Creates the button panel with help, transform, copy, and AI analysis buttons.
     * 
     * @return The configured button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Left side - Help button
        JPanel leftPanel = createLeftButtonPanel();
        panel.add(leftPanel, BorderLayout.WEST);
        
        // Right side - Transform, Copy, and AI Analysis buttons
        JPanel rightPanel = createRightButtonPanel();
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates the left button panel containing the help button.
     * 
     * @return The configured left button panel
     */
    private JPanel createLeftButtonPanel() {
        helpButton = createStyledButton("Help");
        helpButton.setBackground(UIConstants.HELP_COLOR);
        helpButton.addActionListener(e -> showHelpDialog());
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        leftPanel.add(helpButton);
        
        return leftPanel;
    }
    
    /**
     * Creates the right button panel containing transform, copy, and AI analysis buttons.
     * 
     * @return The configured right button panel
     */
    private JPanel createRightButtonPanel() {
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        transformButton = createStyledButton("Transform Code");
        transformButton.addActionListener(e -> handleTransformCode());
        
        copyButton = createStyledButton("Copy to Clipboard");
        copyButton.addActionListener(e -> copyToClipboard());
        
        aiAnalysisButton = createAIAnalysisButton();
        
        rightPanel.add(transformButton);
        rightPanel.add(copyButton);
        rightPanel.add(aiAnalysisButton);
        
        return rightPanel;
    }
    
    /**
     * Creates the AI analysis button with appropriate styling.
     * 
     * @return The configured AI analysis button
     */
    private JButton createAIAnalysisButton() {
        JButton button = createStyledButton("Analyze with AI");
        button.setBackground(UIConstants.AI_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(new CompoundBorder(
            new LineBorder(UIConstants.AI_COLOR, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        button.addActionListener(e -> showAIPlatformSelector());
        
        // Add hover effect for AI button
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(46, 184, 103)); // Slightly lighter green
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIConstants.AI_COLOR);
            }
        });
        
        return button;
    }

    /**
     * Creates a styled button with common properties.
     * 
     * @param text The button text
     * @return The configured button
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.HEADER_FONT);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new LineBorder(UIConstants.PRIMARY_COLOR, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        button.setBackground(UIConstants.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(106, 90, 205)); // Slate Blue
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIConstants.PRIMARY_COLOR);
            }
        });
        
        return button;
    }

    private boolean isDragAcceptable(DropTargetDragEvent dtde) {
        return dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    /**
     * Opens a directory chooser dialog to select a code directory.
     * Updates the UI accordingly when a directory is selected.
     */
    private void browseDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Code Directory");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            directoryField.setText(chooser.getSelectedFile().getAbsolutePath());
            directoryField.setForeground(UIConstants.TEXT_COLOR);
            updateButtonStates();
            showInfo("Directory selected successfully! Click 'Transform Code' to process.");
        }
    }

    /**
     * Handles the transformation of code.
     * This is run in a background thread to keep the UI responsive.
     */
    private void handleTransformCode() {
        String dirPath = directoryField.getText();
        if (dirPath.isEmpty()) {
            showError("Please select a directory first.");
            return;
        }

        // Disable buttons during processing
        transformButton.setEnabled(false);
        aiAnalysisButton.setEnabled(false);
        
        // Show processing message
        showInfo("Analyzing directory: " + dirPath);
        showInfo("Checking codebase size...");
        
        // First check if the codebase is too large
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    int fileCount = directoryProcessor.countCodeFiles(Path.of(dirPath), FileProcessingConfig.MAX_FILES_THRESHOLD + 1);
                    return fileCount > FileProcessingConfig.MAX_FILES_THRESHOLD;
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error counting code files in directory: " + dirPath, e);
                    return false;
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Unexpected error when counting code files: " + dirPath, e);
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean isTooLarge = get();
                    
                    if (isTooLarge) {
                        showCodebaseTooLargeDialog();
                        transformButton.setEnabled(true);
                        aiAnalysisButton.setEnabled(false);
                    } else {
                        // Proceed with transformation
                        processCodebase();
                    }
                } catch (InterruptedException e) {
                    LOGGER.log(Level.WARNING, "Analysis was interrupted", e);
                    showError("Analysis was interrupted");
                    transformButton.setEnabled(true);
                    aiAnalysisButton.setEnabled(false);
                } catch (ExecutionException e) {
                    LOGGER.log(Level.SEVERE, "Error during codebase analysis", e);
                    showError("Error analyzing codebase: " + e.getCause().getMessage());
                    transformButton.setEnabled(true);
                    aiAnalysisButton.setEnabled(false);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Unexpected error during codebase analysis", e);
                    showError("Unexpected error during analysis: " + e.getMessage());
                    transformButton.setEnabled(true);
                    aiAnalysisButton.setEnabled(false);
                }
            }
        }.execute();
    }
    
    /**
     * Shows a dialog informing the user that the codebase is too large.
     */
    private void showCodebaseTooLargeDialog() {
        JDialog dialog = new JDialog(this, "Codebase Too Large", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Add warning icon
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Add warning message
        JLabel warningLabel = new JLabel("Codebase Too Large!");
        warningLabel.setFont(new Font("Arial", Font.BOLD, 18));
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(warningLabel);
        panel.add(Box.createVerticalStrut(10));
        
        // Add explanation
        String message = "<html><div style='text-align: center; width: 400px;'>" +
                         "The selected codebase contains more than " + FileProcessingConfig.MAX_FILES_THRESHOLD + " files, " +
                         "which may cause performance issues or make the application unresponsive.<br><br>" +
                         "Please select a smaller codebase or a specific subdirectory " +
                         "with fewer files for better results.</div></html>";
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(UIConstants.NORMAL_FONT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(messageLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dialog.dispose());
        panel.add(closeButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Processes the codebase after size check passes.
     */
    private void processCodebase() {
        // Show processing message
        showInfo("Processing directory: " + directoryField.getText());
        showInfo("This may take a moment for large codebases...");
        
        // Create a timer to check for long-running operations
        Timer timeoutTimer = new Timer(30000, e -> {
            showWarning("Processing is taking longer than expected. The codebase might be very large.");
        });
        timeoutTimer.setRepeats(false);
        timeoutTimer.start();

        // Run the transformation in a background thread
        new SwingWorker<TransformationResult, Void>() {
            @Override
            protected TransformationResult doInBackground() {
                // Using the already injected processor
                return directoryProcessor.processDirectory(Path.of(directoryField.getText()));
            }

            @Override
            protected void done() {
                timeoutTimer.stop(); // Cancel the timeout timer
                
                try {
                    TransformationResult result = get();
                    handleTransformationResult(result);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.WARNING, "Transformation process was interrupted", e);
                    showError("Transformation process was interrupted");
                } catch (ExecutionException e) {
                    LOGGER.log(Level.SEVERE, "Error during code transformation", e);
                    showError("Error during transformation: " + e.getCause().getMessage());
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Unexpected error during transformation", e);
                    showError("Unexpected error during transformation: " + e.getMessage());
                } finally {
                    // Re-enable buttons
                    transformButton.setEnabled(true);
                    aiAnalysisButton.setEnabled(true);
                }
            }
        }.execute();
    }

    /**
     * Handles the result of a directory transformation.
     * Updates the UI based on success or failure.
     * 
     * @param result The transformation result to handle
     */
    private void handleTransformationResult(TransformationResult result) {
        if (result.isSuccess()) {
            String htmlContent = "<span style='color: #2E8B57;'>" +
                "Transformation complete!</span><br><br>" +
                "Output saved to: " + escapeHtml(result.getOutputPath()) + "<br><br>" +
                "Content preview:<br>" + escapeHtml(result.getContent());
            statusArea.setText(htmlContent);
            showSuccess("Code transformation completed successfully!");
        } else {
            showError(result.getErrorMessage());
        }
    }

    /**
     * Copies the content of the status area to the clipboard.
     * Shows an error if there is no content to copy.
     */
    private void copyToClipboard() {
        String content = statusArea.getText();
        if (!hasValidContent()) {
            showError("No content to copy. Please transform a directory first.");
            return;
        }

        try {
            // Extract actual code content, stripping headers and HTML
            String plainContent = extractCodeContent(content);

            StringSelection selection = new StringSelection(plainContent);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            showSuccess("Content copied to clipboard!");
            LOGGER.log(Level.INFO, "Content successfully copied to clipboard");
        } catch (IllegalStateException e) {
            LOGGER.log(Level.WARNING, "Clipboard system not available", e);
            showError("Could not access system clipboard: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to copy content to clipboard", e);
            showError("Failed to copy content: " + e.getMessage());
        }
    }
    
    /**
     * Extracts only the actual code content from the text, removing headers and HTML formatting.
     * 
     * @param content The raw content from the status area
     * @return Clean code content without headers or HTML
     */
    private String extractCodeContent(String content) {
        // Remove HTML wrapper if present
        String plainContent = content;
        if (plainContent.startsWith("<html>")) {
            plainContent = plainContent.substring(6, plainContent.length() - 7);
        }
        
        // Convert HTML line breaks to newlines
        plainContent = plainContent
            .replaceAll("<br><br>", "\n\n")
            .replaceAll("<br>", "\n");
            
        // Remove all HTML tags
        plainContent = plainContent.replaceAll("<[^>]*>", "");
        
        // Find the index of "Content preview:" which marks the start of the actual code
        int contentStart = plainContent.indexOf("Content preview:");
        if (contentStart >= 0) {
            // Skip the "Content preview:" line by finding the next newline
            int codeStart = plainContent.indexOf('\n', contentStart);
            if (codeStart >= 0) {
                // Extract only the code part (everything after the header)
                plainContent = plainContent.substring(codeStart).trim();
                
                // Split into lines and remove any remaining header lines
                String[] lines = plainContent.split("\n");
                StringBuilder codeBuilder = new StringBuilder();
                
                for (String line : lines) {
                    String trimmedLine = line.trim();
                    // Skip header lines and empty lines
                    if (trimmedLine.contains("Transformation complete") ||
                        trimmedLine.contains("Output saved to") ||
                        trimmedLine.contains("Content preview") ||
                        trimmedLine.isEmpty()) {
                        continue;
                    }
                    codeBuilder.append(line).append('\n');
                }
                
                // Get the final code and trim any trailing whitespace
                plainContent = codeBuilder.toString().trim();
            }
        }
        
        return plainContent;
    }

    /**
     * Checks if there is valid content to copy.
     * 
     * @return true if there is valid content, false otherwise
     */
    private boolean hasValidContent() {
        String content = statusArea.getText();
        if (content == null || content.isEmpty()) {
            return false;
        }

        // Extract content from HTML
        if (content.startsWith("<html>")) {
            content = content.substring(6, content.length() - 7);
        }

        // Remove HTML formatting
        content = content
            .replaceAll("<span style='color: #[0-9A-Fa-f]{6};'>", "")
            .replaceAll("</span>", "")
            .replaceAll("<br><br>", "\n\n")
            .replaceAll("<br>", "\n")
            .trim();

        // Check for distinctive pattern of transformed code content
        boolean hasTransformationMarkers = content.contains("Transformation complete!") && 
                                          content.contains("Output saved to:") && 
                                          content.contains("Content preview:");
                                          
        // If content has transformation markers, it's valid
        if (hasTransformationMarkers) {
            return true;
        }
        
        // Check for commonly occurring patterns in status messages
        boolean isStatusOrInitialMessage = 
            content.isEmpty() ||
            content.equals("Drag and drop a folder here") ||
            content.startsWith("Error:") || 
            content.startsWith("Warning:") ||
            content.contains("Directory selected") ||
            content.contains("Processing directory") ||
            content.contains("Analyzing directory") ||
            content.contains("Checking codebase") ||
            content.contains("Opening") ||
            content.contains("This may take a moment") ||
            content.contains("No content to copy") ||
            content.contains("Code has been copied");
            
        // Only valid if it's not a status message
        return !isStatusOrInitialMessage && content.length() > 100;
    }

    /**
     * Shows an error message in the status area and a dialog.
     * 
     * @param message The error message to show
     */
    private void showError(String message) {
        String htmlMessage = "<span style='color: #B22222;'>Error: " + escapeHtml(message) + "</span>";
        statusArea.setText(htmlMessage);
        updateButtonStates();
        JOptionPane.showMessageDialog(this, message, "Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a success message in a dialog.
     * 
     * @param message The success message to show
     */
    private void showSuccess(String message) {
        updateButtonStates();
        JOptionPane.showMessageDialog(this, message, "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a warning message in the status area and a dialog.
     * 
     * @param message The warning message to show
     */
    private void showWarning(String message) {
        String htmlMessage = "<span style='color: #FFA07A;'>Warning: " + escapeHtml(message) + "</span>";
        statusArea.setText(htmlMessage);
        updateButtonStates();
        JOptionPane.showMessageDialog(this, message, "Warning", 
            JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Shows an informational message in the status area.
     * 
     * @param message The informational message to show
     */
    private void showInfo(String message) {
        String htmlMessage = "<span style='color: #323232;'>" + escapeHtml(message) + "</span>";
        statusArea.setText(htmlMessage);
        updateButtonStates();
    }

    /**
     * Escapes HTML special characters in a string.
     * 
     * @param text The text to escape
     * @return The escaped text
     */
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\n", "<br>");
    }

    /**
     * Updates the enabled state of buttons based on application state.
     */
    private void updateButtonStates() {
        boolean hasDirectory = !directoryField.getText().isEmpty() && 
                             !directoryField.getText().equals("Drag and drop a folder here");
        
        boolean hasContent = hasValidContent();
        
        transformButton.setEnabled(hasDirectory);
        copyButton.setEnabled(hasContent);
        aiAnalysisButton.setEnabled(hasContent);
    }

    private void showAIPlatformSelector() {
        // Create a custom dialog
        JDialog dialog = new JDialog(this, "Choose AI Platform", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Create a panel for the platforms
        JPanel platformPanel = new JPanel();
        platformPanel.setLayout(new BoxLayout(platformPanel, BoxLayout.Y_AXIS));
        platformPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        platformPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Add a description label
        JLabel descLabel = new JLabel("Select an AI platform to analyze your code:");
        descLabel.setFont(UIConstants.HEADER_FONT);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        platformPanel.add(descLabel);
        platformPanel.add(Box.createVerticalStrut(20));

        // Create buttons for each platform
        String[] platforms = AIPlatformURLs.getSupportedPlatforms();
        for (String platform : platforms) {
            JButton button = createPlatformButton(platform, dialog);
            platformPanel.add(button);
            platformPanel.add(Box.createVerticalStrut(10));
        }

        // Add cancel button at the bottom
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        buttonPanel.add(cancelButton);
        platformPanel.add(Box.createVerticalStrut(20));
        platformPanel.add(buttonPanel);
        
        dialog.add(platformPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JButton createPlatformButton(String platform, JDialog parentDialog) {
        JButton button = new JButton(platform);
        button.setFont(UIConstants.HEADER_FONT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(200, 40));

        // Style the button based on the platform
        switch (platform.toLowerCase()) {
            case "chatgpt":
                button.setBackground(new Color(10, 132, 255));
                break;
            case "gemini":
                button.setBackground(new Color(66, 133, 244));
                break;
            case "copilot":
                button.setBackground(new Color(36, 41, 47));
                break;
            case "codeium":
                button.setBackground(new Color(0, 168, 150));
                break;
            default:
                button.setBackground(UIConstants.PRIMARY_COLOR);
        }
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new LineBorder(button.getBackground().darker(), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(button.getBackground().darker());
            }
        });

        // Add click handler
        button.addActionListener(e -> {
            parentDialog.dispose();
            handleAIPlatformSelection(platform);
        });

        return button;
    }

    /**
     * Handles the selection of an AI platform.
     * Opens the platform in a browser with the code content.
     * Also copies the code to clipboard for easy pasting.
     * 
     * @param selectedPlatform The selected AI platform
     */
    private void handleAIPlatformSelection(String selectedPlatform) {
        String codeContent = statusArea.getText();
        if (codeContent == null || codeContent.trim().isEmpty()) {
            showError("No content to send to AI platform. Please transform a directory first.");
            return;
        }
        
        // Clean the content by removing HTML tags
        codeContent = codeContent.replaceAll("<[^>]*>", "").trim();
        
        try {
            // First open the browser
            URI aiUrl = AIPlatformURLs.generateAIURL(selectedPlatform.toLowerCase(), codeContent);
            
            // Try to open using our enhanced browser launcher
            BrowserLauncher.BrowserLaunchResult result = BrowserLauncher.openURL(aiUrl);
            
            if (result.isSuccess()) {
                showInfo("Opening " + selectedPlatform + " for AI analysis...");
            } else {
                // If browser cannot be opened, show the URL in a clickable dialog
                showUrlDialog(aiUrl, selectedPlatform);
                showInfo("Browser could not be launched: " + result.getErrorMessage());
                LOGGER.log(Level.WARNING, "Browser could not be launched: {0}", result.getErrorMessage());
            }
            
            // Display the URL in the status area as a clickable link
            displayClickableLink(aiUrl, selectedPlatform);
            
            // AFTER browser is opened, copy the content to clipboard
            // This ensures the browser doesn't overwrite our clipboard content
            copyCodeToClipboard(codeContent, selectedPlatform);
            
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error when accessing platform: " + selectedPlatform, e);
            showError("Error accessing " + selectedPlatform + ": " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to open AI platform: " + selectedPlatform, e);
            showError("Failed to open AI platform: " + e.getMessage());
        }
    }
    
    /**
     * Copies code to the clipboard and shows a confirmation message.
     * 
     * @param code The code to copy
     * @param platformName The name of the platform for the confirmation message
     */
    private void copyCodeToClipboard(String code, String platformName) {
        // Extract only the actual code content
        String tempCode = extractCodeContent(code);
        
        // Make final for lambda
        final String cleanedCode = tempCode;
        
        // Use a small delay to ensure the clipboard operation happens after browser launch
        Timer clipboardTimer = new Timer(500, e -> {
            try {
                StringSelection selection = new StringSelection(cleanedCode);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                showInfo("Code has been copied to clipboard for easy pasting into " + platformName);
            } catch (Exception ex) {
                showError("Failed to copy code to clipboard: " + ex.getMessage());
            }
        });
        clipboardTimer.setRepeats(false);
        clipboardTimer.start();
    }

    /**
     * Shows a dialog with the URL when the browser cannot be opened directly.
     * 
     * @param url The URL to display
     * @param platformName The name of the platform
     */
    private void showUrlDialog(URI url, String platformName) {
        // Create components
        JDialog dialog = new JDialog(this, platformName + " URL", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Add a description label
        JLabel messageLabel = new JLabel("Click the link below to open " + platformName + ":");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JEditorPane linkPane = new JEditorPane();
        linkPane.setContentType("text/html");
        linkPane.setEditable(false);
        linkPane.setText("<html><center><a href='" + url + "'>" + url + "</a></center></html>");
        linkPane.setBackground(panel.getBackground());
        
        // Add hyperlink listener
        linkPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                    dialog.dispose();
                } catch (Exception ex) {
                    showError("Failed to open link: " + ex.getMessage());
                }
            }
        });
        
        JButton copyButton = new JButton("Copy URL");
        copyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        copyButton.addActionListener(e -> {
            StringSelection selection = new StringSelection(url.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            showSuccess("URL copied to clipboard!");
        });
        
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dialog.dispose());
        
        // Add components
        panel.add(messageLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(linkPane);
        panel.add(Box.createVerticalStrut(10));
        panel.add(copyButton);
        panel.add(Box.createVerticalStrut(5));
        panel.add(closeButton);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Displays a clickable link in the status area.
     * 
     * @param url The URL to display
     * @param platformName The name of the platform for display purposes
     */
    private void displayClickableLink(URI url, String platformName) {
        String urlString = url.toString();
        
        // Create a clickable link HTML
        String linkHtml = "<br><br>" + platformName + " URL:<br>" +
                          "<a href='" + urlString + "'>" + urlString + "</a>";
        
        // Copy to clipboard for convenience
        StringSelection selection = new StringSelection(urlString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        
        // Append the link to the status area
        appendToStatusArea("\n\n" + linkHtml);
    }

    /**
     * Appends text to the status area without overwriting existing content.
     * 
     * @param text The text to append
     */
    private void appendToStatusArea(String text) {
        try {
            // Get current content
            String currentContent = statusArea.getText();
            
            // Remove HTML tags if present
            if (currentContent.startsWith("<html>")) {
                currentContent = currentContent.substring(6, currentContent.length() - 7);
            }
            
            // Append new content
            String newContent = currentContent + escapeHtml(text);
            
            // Set the updated content
            statusArea.setText("<html>" + newContent + "</html>");
            
            // Scroll to the bottom to show the appended text
            statusArea.setCaretPosition(statusArea.getDocument().getLength());
        } catch (Exception e) {
            // Fallback to simple setText if there's an error
            statusArea.setText("<html>" + escapeHtml(text) + "</html>");
        }
    }
    
    /**
     * Shows a help dialog with information about the application.
     */
    private void showHelpDialog() {
        JDialog helpDialog = new JDialog(this, "Code Transformer Help", true);
        helpDialog.setSize(600, 500);
        helpDialog.setLocationRelativeTo(this);

        JPanel contentPanel = createHelpDialogContentPanel();
        helpDialog.add(contentPanel);
        helpDialog.setVisible(true);
    }
    
    /**
     * Creates the content panel for the help dialog.
     * 
     * @return The configured content panel
     */
    private JPanel createHelpDialogContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Help content
        JTextPane helpContent = createHelpTextPane();
        JScrollPane scrollPane = new JScrollPane(helpContent);
        scrollPane.setBorder(null);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Close button
        JButton closeButton = createStyledButton("Close");
        closeButton.addActionListener(e -> ((JDialog)SwingUtilities.getWindowAncestor(contentPanel)).dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        buttonPanel.add(closeButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return contentPanel;
    }
    
    /**
     * Creates the help text pane with formatted HTML content.
     * 
     * @return The configured help text pane
     */
    private JTextPane createHelpTextPane() {
        JTextPane helpContent = new JTextPane();
        helpContent.setContentType("text/html");
        helpContent.setEditable(false);
        helpContent.setBackground(UIConstants.BACKGROUND_COLOR);
        helpContent.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        helpContent.setFont(UIConstants.NORMAL_FONT);
        
        String helpText = getHelpDialogHtmlContent();
        helpContent.setText(helpText);
        helpContent.setCaretPosition(0);
        
        return helpContent;
    }
    
    /**
     * Returns the HTML content for the help dialog.
     * 
     * @return Formatted HTML content
     */
    private String getHelpDialogHtmlContent() {
        return "<html><body style='width: 400px; font-family: Dialog;'>" +
            "<h1 style='color: #1976D2;'>Code Transformer Help</h1>" +
            "<h2 style='color: #1976D2;'>Overview</h2>" +
            "<p>Code Transformer helps you combine all code files from a directory into a single text file, " +
            "making it easier to share your codebase with AI tools for analysis and interpretation.</p>" +
            
            "<h2 style='color: #1976D2;'>AI Integration</h2>" +
            "<p><b>Using the Output with AI Tools:</b></p>" +
            "<ul>" +
            "<li>The generated text file is optimized for AI analysis</li>" +
            "<li>You can either:</li>" +
            "<ul>" +
            "   <li>Upload the saved text file directly to AI platforms that support file uploads</li>" +
            "   <li>Use the 'Copy to Clipboard' button and paste the content into any AI chat interface</li>" +
            "   <li>Click on any AI platform button (ChatGPT, Gemini, etc.) to automatically open the platform and copy the code to clipboard</li>" +
            "</ul>" +
            "<li>File paths are included to help AIs understand the project structure</li>" +
            "<li>Only code files are included, making it easier for AIs to focus on implementation details</li>" +
            "</ul>" +
            
            "<h2 style='color: #1976D2;'>Supported File Types</h2>" +
            "<p><b>The application processes the following types of files:</b></p>" +
            "<ul>" +
            "<li>Programming Languages: Java, Python, JavaScript, TypeScript, C++, C, Go, Rust, Ruby</li>" +
            "<li>Web Development: PHP, Scala, Kotlin, Groovy, Swift</li>" +
            "<li>Shell Scripts: sh, bash, PowerShell, batch</li>" +
            "<li>Other: R, Perl, SQL, Lua, Elm, Erlang, Elixir</li>" +
            "</ul>" +
            
            "<h2 style='color: #1976D2;'>How to Use</h2>" +
            "<ol>" +
            "<li><b>Select Directory:</b><br>" +
            "   • Drag and drop a folder into the input field, or<br>" +
            "   • Click the 'Browse' button to select a folder</li>" +
            "<li><b>Transform Code:</b><br>" +
            "   • Click 'Transform Code' to process the files<br>" +
            "   • Wait for the process to complete</li>" +
            "<li><b>Get Results:</b><br>" +
            "   • The output file will be saved as '[folder]_code_only.txt'<br>" +
            "   • Click 'Copy to Clipboard' to copy the content for direct use with AI tools<br>" +
            "   • Or click any AI platform button to open that platform and automatically copy the code to clipboard</li>" +
            "</ol>" +
            
            "<h2 style='color: #1976D2;'>AI-Friendly Features</h2>" +
            "<ul>" +
            "<li>Clean output format that AIs can easily parse</li>" +
            "<li>Automatic filtering of non-code files (e.g., binaries, images)</li>" +
            "<li>Clear file separation with path information</li>" +
            "<li>Consistent encoding for reliable AI processing</li>" +
            "<li>Hierarchical structure preservation</li>" +
            "<li>One-click AI platform integration with automatic clipboard copying</li>" +
            "</ul>" +
            
            "<h2 style='color: #1976D2;'>Tips for AI Analysis</h2>" +
            "<ul>" +
            "<li>Consider splitting very large codebases into logical chunks</li>" +
            "<li>Include key files that provide context (e.g., main classes, core utilities)</li>" +
            "<li>Use the clipboard feature for quick AI chat interactions</li>" +
            "<li>Save the file for larger codebases or AI platforms with file upload support</li>" +
            "<li>When using the AI platform buttons, the code is automatically copied to your clipboard for easy pasting</li>" +
            "</ul>" +
            "</body></html>";
    }

    /**
     * Sets up the drop target for the main window.
     * Allows users to drag and drop folders onto the window.
     */
    private void setupDropTarget() {
        // Setup drop target
        new DropTarget(this, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked") // Suppress unchecked cast warning
                    List<File> droppedFiles = (List<File>) dtde.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    
                    processDroppedFiles(droppedFiles);
                } catch (Exception e) {
                    showError("Error processing dropped files: " + e.getMessage());
                }
            }
        });
    }
}
