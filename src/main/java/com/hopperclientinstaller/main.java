package com.hopperclientinstaller;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class hcimain {
    private static JTextArea textArea;
    private static JFrame frame;
    private static JButton finishButton;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
        new Thread(() -> {
            try {
                runScript("install_node.sh");
                runScript("install_java.sh");
                SwingUtilities.invokeLater(() -> finishInstallation());
            } catch (IOException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showError(e.getMessage()));
            }
        }).start();
    }

    private static void createAndShowGUI() {
        frame = new JFrame("Dependency Installer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        finishButton = new JButton("Finish");
        finishButton.setEnabled(false);
        finishButton.addActionListener(e -> openAnotherApp());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(finishButton, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void runScript(String scriptPath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(scriptPath);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                updateTextArea(line);
            }
        }
    }

    private static void updateTextArea(String text) {
        SwingUtilities.invokeLater(() -> textArea.append(text + "\n"));
    }

    private static void finishInstallation() {
        textArea.append("\nInstallation completed!\n");
        finishButton.setEnabled(true);
    }

    private static void showError(String error) {
        textArea.append("\nError: " + error + "\n");
    }

    private static void openAnotherApp() {
            // String removed for security reasons
        try {
            new ProcessBuilder(otherAppPath).start();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to open the application.");
        }
    }
}
