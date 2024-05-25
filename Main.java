import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;


public class Main {

  public static void main(String[] args) throws FileNotFoundException {
    ArrayList<Point> points = new ArrayList<>();
    Scanner scanner = new Scanner(new File("rangepoints.txt"));

    while (scanner.hasNext()) {
      double x = scanner.nextDouble();
      double y = scanner.nextDouble();
      points.add(new Point(x, y));
    }

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
  }
}
