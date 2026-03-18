package com.tsp;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.tsp.model.City;


/**
 * Parses TSPLIB .tsp files from local files or URLs.
 */
public class ParseFile {

    /**
     * Load cities from a local file.
     * @param file Local .tsp file
     * @return List of City objects
     * @throws IOException If file cannot be read or parsed
     */
    public static List<City> loadFromFile(File file) throws IOException {
        return parse(new BufferedReader(new FileReader(file)));
    }

    /**
     * Load cities from a URL pointing to a .tsp file.
     * @param urlString Direct URL to a .tsp file
     * @return List of City objects
     * @throws IOException If URL cannot be read or parsed
     */
    public static List<City> loadFromURL(String urlString) throws IOException {
        URL url = new URL(urlString);
        return parse(new BufferedReader(new InputStreamReader(url.openStream())));
    }

    /**
     * Shared parsing logic for both file and URL inputs.
     */
    private static List<City> parse(BufferedReader br) throws IOException {
        List<City> cities = new ArrayList<>();
        String line;
        boolean inNodes = false;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (!inNodes) {
                if (line.equalsIgnoreCase("NODE_COORD_SECTION")) {
                    inNodes = true;
                }
                continue;
            }

            if (line.equalsIgnoreCase("EOF")
                    || line.equalsIgnoreCase("DISPLAY_DATA_SECTION")
                    || line.equalsIgnoreCase("EDGE_WEIGHT_SECTION")) {
                break;
            }

            String[] parts = line.split("\\s+");
            if (parts.length < 3) continue;

            int id = Integer.parseInt(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            cities.add(new City(id, x, y));
        }

        if (cities.isEmpty()) {
            throw new IOException("No cities found. Make sure the file or URL points to a valid .tsp file.");
        }

        return cities;
    }
}