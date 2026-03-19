package com.tsp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.tsp.Coordinator;
import com.tsp.ParseFile;
import com.tsp.core.NearestNeighbors;
import com.tsp.model.City;

/**
 * GUI for Distributed TSP Solver
 */
public class TspFrame extends JFrame {

    private final MapPanel mapPanel = new MapPanel();
    private final JTextArea log = new JTextArea(8, 60);
    private final Coordinator coordinator = new Coordinator();

    private List<City> cities = new ArrayList<>();
    private List<Integer> tour = new ArrayList<>();

    private final JTextField urlField = new JTextField("Enter TSP file URL here...", 25);

    public TspFrame() {
        super("Distributed TSP Solver");

        // Log area
        log.setEditable(false);
        log.setBackground(new Color(200, 255, 220));

        // Buttons
        JButton loadFileBtn = new JButton("Load Local File");
        JButton loadUrlBtn = new JButton("Load from URL");
        JButton solveBtn = new JButton("Nearest Neighbor (Local)");
        JButton clearBtn = new JButton("Clear Tour");
        JButton distributeBtn = new JButton("Distribute Tasks");
        JButton drawBestBtn = new JButton("Draw Best Tour");

        // Top panel with 2 rows
        JPanel top = new JPanel(new GridLayout(2, 1));

        // Row 1: Load buttons + URL
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(loadFileBtn);
        row1.add(loadUrlBtn);
        row1.add(urlField);

        // Row 2: Solve, Clear, Distribute, Draw
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(solveBtn);
        row2.add(clearBtn);
        row2.add(distributeBtn);
        row2.add(drawBestBtn);

        top.add(row1);
        top.add(row2);

        // Layout
        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);
        add(new JScrollPane(log), BorderLayout.SOUTH);

        log.append("Ready: Load a TSPLIB file (local or URL).\n");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Action listeners
        loadFileBtn.addActionListener(e -> onLoadFile());
        loadUrlBtn.addActionListener(e -> onLoadURL());
        solveBtn.addActionListener(e -> onSolveLocal());
        clearBtn.addActionListener(e -> onClear());
        distributeBtn.addActionListener(e -> onDistribute());
        drawBestBtn.addActionListener(e -> onDrawBest());
    }

    /** Load cities from a local file */
    private void onLoadFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a TSPLIB .tsp file");
        chooser.setFileFilter(new FileNameExtensionFilter("TSPLIB (*.tsp)", "tsp"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();
        try {
            cities = ParseFile.loadFromFile(f);
            tour = new ArrayList<>();
            mapPanel.setCities(cities);
            coordinator.setCities(cities);
            log.append("\nLoaded local file: " + f.getAbsolutePath() + "\n");
            log.append("Cities: " + cities.size() + "\n");
        } catch (Exception ex) {
            log.append("\nERROR loading local file: " + ex.getMessage() + "\n");
        }
    }

    /** Load cities from a URL */
    private void onLoadURL() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            log.append("\nPlease enter a URL.\n");
            return;
        }
        try {
            cities = ParseFile.loadFromURL(url);
            tour = new ArrayList<>();
            mapPanel.setCities(cities);
            coordinator.setCities(cities);
            log.append("\nLoaded from URL: " + url + "\n");
            log.append("Cities: " + cities.size() + "\n");
        } catch (Exception ex) {
            log.append("\nERROR loading from URL: " + ex.getMessage() + "\n");
        }
    }

    /** Solve locally using nearest neighbor */
    private void onSolveLocal() {
        if (cities == null || cities.size() < 2) {
            log.append("\nLoad a file or URL first.\n");
            return;
        }
        tour = NearestNeighbors.solve(cities, 0);
        double len = NearestNeighbors.length(cities, tour);
        mapPanel.setTour(tour);
        log.append("\nNearest-neighbor tour computed (local).\n");
        log.append("Tour length (Euclidean): " + String.format("%.3f", len) + "\n");
    }

    /** Clear tour */
    private void onClear() {
        tour.clear();
        mapPanel.setTour(tour);

        // Clear coordinator best tour
        synchronized (coordinator.getBestTour()) {
            coordinator.getBestTour().clear();
        }

        log.append("\nTour cleared. Ready for new file or URL.\n");
        mapPanel.repaint();
    }

    /** Send tasks to workers via Coordinator */
    private void onDistribute() {
        if (cities == null || cities.isEmpty()) {
            log.append("\nLoad a file or URL first.\n");
            return;
        }

        log.append("\nDistributing tasks to workers...\n");
        new Thread(() -> {
            coordinator.distributeTasks();
            SwingUtilities.invokeLater(() ->
                log.append("All tasks have been published.\n")
            );
        }).start();
    }

    /** Draw the best tour collected so far from workers */
    private void onDrawBest() {
        List<Integer> bestTour = coordinator.getBestTour();
        if (bestTour == null || bestTour.isEmpty()) {
            log.append("\nNo tour received yet from workers.\n");
            return;
        }
        mapPanel.setTour(bestTour);
        double length = coordinator.getBestLength();
        log.append("\nBest tour drawn. Length: " + String.format("%.3f", length) + "\n");
    }

    /** Main */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TspFrame().setVisible(true));
    }
}