package com.codetransformer.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Constants for UI components.
 * Centralizes all UI-related constants for better maintainability.
 */
public final class UIConstants {
    private UIConstants() {
        throw new AssertionError("Constants class should not be instantiated");
    }

    // UI Colors - grouped by purpose for better organization
    public static final Color BACKGROUND_COLOR = new Color(220, 220, 220);
    public static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    public static final Color SUCCESS_COLOR = new Color(46, 139, 87);
    public static final Color TEXT_COLOR = new Color(50, 50, 50);
    public static final Color HELP_COLOR = new Color(100, 149, 237);
    public static final Color AI_COLOR = new Color(38, 166, 91);
    
    // Title gradient colors - Dark Green palette
    public static final Color TITLE_COLOR_1 = new Color(0, 100, 0);      // Dark Green
    public static final Color TITLE_COLOR_2 = new Color(34, 139, 34);    // Forest Green
    public static final Color TITLE_COLOR_3 = new Color(46, 139, 87);    // Sea Green
    public static final Color TITLE_COLOR_4 = new Color(60, 179, 113);   // Medium Sea Green
    public static final Color TITLE_SHADOW_COLOR = new Color(40, 40, 40, 100);
    
    // UI Fonts
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 14);
    
    // Dimensions and Sizes
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 500;
    public static final int BUTTON_PADDING = 15;
    public static final int PANEL_PADDING = 20;
    
    // Border Constants
    public static final int BORDER_THICKNESS = 1;
    public static final int BORDER_RADIUS = 15;
    
    // Animation Constants
    public static final int HOVER_ANIMATION_DURATION = 200;
    
    // Resource Paths
    public static final String LOGO_PATH = "/images/logo.jpeg";
} 