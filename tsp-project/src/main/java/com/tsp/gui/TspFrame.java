package com.tsp.gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import com.tsp.Coordinator;
import com.tsp.model.City;
import com.tsp.core.NearestNeighbors;
import com.tsp.ParseFile;

import java.util.ArrayList;




/**
 * Updated GUI for loading TSPLIB .tsp files from local files or URLs,
 * displaying the cities, and integrating with the Coordinator for distributed TSP.
 */
public class TspFrame extends JFrame {

    private final MapPanel mapPanel = new MapPanel();
    private final JTextArea log = new JTextArea(8, 60);
    private final Coordinator coordinator = new Coordinator();

    private List<City> cities = new ArrayList<>();
    private List<Integer> tour = new ArrayList<>();

    private final JTextField urlField = new JTextField("Enter TSP file URL here...", 40);

    public TspFrame() {
        super("Distributed TSP Solver");
        log.setEditable(false);
        log.setBackground(new Color(200, 255, 220));

        // Buttons
        JButton loadFileBtn = new JButton("Load Local File");
        JButton loadUrlBtn = new JButton("Load from URL");
        JButton solveBtn = new JButton("Nearest Neighbor (Local)");
        JButton clearBtn = new JButton("Clear Tour");

        // Top panel
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(loadFileBtn);
        top.add(urlField);
        top.add(loadUrlBtn);
        top.add(solveBtn);
        top.add(clearBtn);

        // Layout
        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);
        add(new JScrollPane(log), BorderLayout.SOUTH);

        // Default messages
        log.append("Ready: Load a TSPLIB file (local or URL).\n");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Action listeners
        loadFileBtn.addActionListener(e -> onLoadFile());
        loadUrlBtn.addActionListener(e -> onLoadURL());
        solveBtn.addActionListener(e -> onSolveLocal());
        clearBtn.addActionListener(e -> onClear());
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
        tour = new ArrayList<>();
        mapPanel.setTour(tour);
        log.append("\nTour cleared.\n");
    }

    /** Main */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TspFrame().setVisible(true));
    }
}