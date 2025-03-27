package com.codetransformer.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Factory class for creating UI components.
 * Follows Single Responsibility Principle by handling only component creation.
 */
public class UIComponentFactory {
    private UIComponentFactory() {
        throw new AssertionError("Factory class should not be instantiated");
    }

    /**
     * Creates a styled button with common properties.
     *
     * @param text The button text
     * @param backgroundColor The background color
     * @param hoverColor The color to use when hovering
     * @return The configured button
     */
    public static JButton createStyledButton(String text, Color backgroundColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.HEADER_FONT);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new LineBorder(backgroundColor, UIConstants.BORDER_THICKNESS),
            new EmptyBorder(8, UIConstants.BUTTON_PADDING, 8, UIConstants.BUTTON_PADDING)
        ));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }

    /**
     * Creates a styled button with primary colors.
     *
     * @param text The button text
     * @return The configured button
     */
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, UIConstants.PRIMARY_COLOR, new Color(106, 90, 205));
    }

    /**
     * Creates a styled button for AI functionality.
     *
     * @param text The button text
     * @return The configured button
     */
    public static JButton createAIButton(String text) {
        return createStyledButton(text, UIConstants.AI_COLOR, new Color(46, 184, 103));
    }

    /**
     * Creates a styled text field for directory input.
     *
     * @param placeholder The placeholder text
     * @return The configured text field
     */
    public static JTextField createDirectoryField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(UIConstants.NORMAL_FONT);
        field.setEditable(false);
        field.setBackground(Color.WHITE);
        field.setBorder(new CompoundBorder(
            new LineBorder(UIConstants.PRIMARY_COLOR, UIConstants.BORDER_THICKNESS),
            new EmptyBorder(8, 10, 8, 10)
        ));
        field.setForeground(Color.GRAY);
        return field;
    }

    /**
     * Creates a styled text pane for status display.
     *
     * @return The configured text pane
     */
    public static JTextPane createStatusPane() {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setFont(UIConstants.NORMAL_FONT);
        pane.setContentType("text/html");
        pane.setBorder(new CompoundBorder(
            new LineBorder(UIConstants.PRIMARY_COLOR.brighter(), UIConstants.BORDER_THICKNESS),
            new EmptyBorder(5, 5, 5, 5)
        ));
        pane.setBackground(Color.WHITE);
        return pane;
    }

    /**
     * Creates a scroll pane for text components.
     *
     * @param component The component to wrap
     * @param height The preferred height
     * @return The configured scroll pane
     */
    public static JScrollPane createScrollPane(JComponent component, int height) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, height));
        return scrollPane;
    }

    /**
     * Creates a panel with the standard background color.
     *
     * @param layout The layout manager to use
     * @return The configured panel
     */
    public static JPanel createPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        return panel;
    }

    /**
     * Creates a header label with standard styling.
     *
     * @param text The label text
     * @return The configured label
     */
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.HEADER_FONT);
        label.setForeground(UIConstants.TEXT_COLOR);
        return label;
    }
} 