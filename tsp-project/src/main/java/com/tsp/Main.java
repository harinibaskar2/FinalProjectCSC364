package com.tsp;

import javax.swing.SwingUtilities;

import com.tsp.gui.TspFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TspFrame frame = new TspFrame();
            frame.setVisible(true);
        });
        System.out.println("Distributed TSP GUI/Coordinator started.");
    }
}




