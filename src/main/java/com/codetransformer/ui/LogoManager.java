package com.codetransformer.ui;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages logo loading and rendering operations.
 * Follows Single Responsibility Principle by handling only logo-related operations.
 */
public class LogoManager {
    private static final Logger LOGGER = Logger.getLogger(LogoManager.class.getName());
    
    private BufferedImage logoImage;
    private final int logoSize;
    private final Shape logoShape;

    /**
     * Creates a new LogoManager with the specified logo size.
     *
     * @param size The size of the logo in pixels
     */
    public LogoManager(int size) {
        this.logoSize = size;
        this.logoShape = new Ellipse2D.Float(0, 0, size, size);
        loadLogo();
    }

    /**
     * Loads the logo from resources.
     */
    private void loadLogo() {
        try {
            logoImage = ImageIO.read(getClass().getResourceAsStream(UIConstants.LOGO_PATH));
            if (logoImage == null) {
                LOGGER.warning("Failed to load logo: Image is null");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading logo", e);
        }
    }

    /**
     * Creates a panel that displays the logo.
     *
     * @return A JPanel containing the logo
     */
    public JPanel createLogoPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                try {
                    // Enable antialiasing
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Set the clip to the logo shape
                    g2d.setClip(logoShape);
                    
                    if (logoImage != null) {
                        // Draw the image
                        g2d.drawImage(logoImage, 0, 0, logoSize, logoSize, null);
                        
                        // Draw a subtle border
                        g2d.setColor(new Color(0, 0, 0, 50));
                        g2d.setStroke(new BasicStroke(2f));
                        g2d.draw(logoShape);
                    } else {
                        // Draw placeholder
                        g2d.setColor(Color.WHITE);
                        g2d.fillOval(0, 0, logoSize, logoSize);
                        g2d.setColor(Color.GRAY);
                        g2d.drawOval(0, 0, logoSize, logoSize);
                    }
                } finally {
                    g2d.dispose();
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(logoSize, logoSize);
            }
        };
    }

    /**
     * Creates a container panel for the logo with proper padding and alignment.
     *
     * @return A JPanel containing the logo with padding
     */
    public JPanel createLogoContainer() {
        JPanel logoPanel = createLogoPanel();
        logoPanel.setOpaque(false);
        
        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        container.setOpaque(false);
        container.add(logoPanel);
        
        return container;
    }
} 