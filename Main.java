import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;




public class Main {

    public static ArrayList<Point> readPointsFromFile(String fileName) throws IOException {
        ArrayList<Point> points = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null) {
            String[] coordinates = line.split("\\s+");
            double x = Double.parseDouble(coordinates[0]);
            double y = Double.parseDouble(coordinates[1]);
            points.add(new Point(x, y));
        }
        br.close();
        return points;
    }

  public static void main(String[] args) {
      try {

    ArrayList<Point> points = readPointsFromFile("rangepoints.txt");

    RangeTree rangeTree = new RangeTree(points);

    /*
    System.out.println("Enter search range (minX maxX minY maxY):");
    Scanner consoleScanner = new Scanner(System.in);
    double minX = consoleScanner.nextDouble();
    double maxX = consoleScanner.nextDouble();
    double minY = consoleScanner.nextDouble();
    double maxY = consoleScanner.nextDouble();
    */
    // Example range query
    double minX = 1.0, minY = 1.0, maxX = 100.0, maxY = 100.0;
    BoundingBox range = new BoundingBox(minX, maxX, minY, maxY);
    List<Point> results = rangeTree.rangeQuery(range);

    System.out.println("Points within the range:");
    for (Point point : results) {
      System.out.println("(" + point.x + ", " + point.y + ")");
    }

    { // visualization
        // Create Swing window
        JFrame frame = new JFrame("2D Range Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        // Create drawing panel
        DrawingPanel drawingPanel = new DrawingPanel(rangeTree, range);

        // Add drawing panel to the frame
        frame.add(drawingPanel);

        // Show the frame
        frame.setVisible(true);
    }
  } catch (IOException e) {
        System.err.println("Error reading points from file: " + e.getMessage());
    }
}
}
