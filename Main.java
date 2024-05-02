import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;

class Point {
    double x;
    double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class Node {
    Point point;
    Node left;
    Node right;

    public Node(Point point) {
        this.point = point;
        this.left = null;
        this.right = null;
    }
}

class RangeTree {
    Node root;
    public double minX, minY, maxX, maxY;

    public RangeTree(List<Point> points) {
        this.root = constructRangeTree(points, 0, points.size() - 1, true);
        findBoundingBox(root);
    }

    private Node constructRangeTree(List<Point> points, int start, int end, boolean byX) {
        if (start > end)
            return null;

        if (start == end)
            return new Node(points.get(start));

        if (byX)
            points.sort((p1, p2) -> Double.compare(p1.x, p2.x));
        else
            points.sort((p1, p2) -> Double.compare(p1.y, p2.y));

        int mid = (start + end) / 2;
        Node node = new Node(points.get(mid));
        node.left = constructRangeTree(points, start, mid - 1, !byX);
        node.right = constructRangeTree(points, mid + 1, end, !byX);

        return node;
    }

    private void findBoundingBox(Node node) {
        if (node == null)
            return;

        Point point = node.point;
        minX = Math.min(minX, point.x);
        minY = Math.min(minY, point.y);
        maxX = Math.max(maxX, point.x);
        maxY = Math.max(maxY, point.y);

        findBoundingBox(node.left);
        findBoundingBox(node.right);
    }

    public List<Point> queryRange(double x1, double y1, double x2, double y2) {
        List<Point> result = new ArrayList<>();
        queryRangeHelper(root, x1, y1, x2, y2, true, result);
        return result;
    }

    private void queryRangeHelper(Node node, double x1, double y1, double x2, double y2, boolean byX, List<Point> result) {
        if (node == null)
            return;

        if (node.point.x >= x1 && node.point.x <= x2 && node.point.y >= y1 && node.point.y <= y2)
            result.add(node.point);

        if (byX) {
            if (node.point.x >= x1)
                queryRangeHelper(node.left, x1, y1, x2, y2, !byX, result);
            if (node.point.x <= x2)
                queryRangeHelper(node.right, x1, y1, x2, y2, !byX, result);
        } else {
            if (node.point.y >= y1)
                queryRangeHelper(node.left, x1, y1, x2, y2, !byX, result);
            if (node.point.y <= y2)
                queryRangeHelper(node.right, x1, y1, x2, y2, !byX, result);
        }
    }
}


class DrawingPanel extends JPanel {
    private List<Point> points;
    public double x1, y1, x2, y2;
    private double minX, minY, maxX, maxY;

    public DrawingPanel(List<Point> points, double minX, double minY, double maxX, double maxY) {
        this.points = points;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        setPreferredSize(new Dimension(600, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

	double pointsWidth = maxX - minX;
	double pointsHeight = maxY - minY;

        // Draw points
        for (Point point : points) {
            int px = (int) ((point.x - minX) / pointsWidth * getWidth());
            int py = (int) ((maxY - point.y) / pointsHeight * getHeight());
            //int py = (int) ((point.y - minY) / pointsHeight * getHeight());
            g2d.setColor(Color.BLACK);
            g2d.fillOval(px - 3, py - 3, 6, 6);
        }

	float thickness = 3;
	Stroke oldStroke = g2d.getStroke();
	g2d.setStroke(new BasicStroke(thickness));

        // Draw range query rectangle
        //int rx1 = (int) ((x1 - minX) / pointsWidth * getWidth());
        //int ry1 = (int) ((maxY - y1) / pointsHeight * getHeight());
        //int rx2 = (int) ((x2 - minX) / pointsWidth * getWidth());
        //int ry2 = (int) ((maxY - y2) / pointsHeight * getHeight());
        int rx1 = (int) (((x1 - minX) / pointsWidth) * getWidth());
	int rx2 = (int) (((x2 - minX) / pointsWidth) * getWidth());
        int ry1 = (int) (((y1 - minY) / pointsHeight) * getHeight());
	int ry2 = (int) (((y2 - minY) / pointsHeight) * getHeight());
        g2d.setColor(Color.RED);
        //g2d.drawRect(rx1, ry1, rx2 - rx1, ry2 - ry1);
        g2d.drawRect(rx1, getHeight() - ry2, rx2 - rx1, ry2 - ry1);
	//System.out.println("x1=" + x1 + " x2=" + x2 + " y1=" + y1 + " y2=" + y2);
	//System.out.println("rx1=" + rx1 + " rx2=" + rx2 + " ry1=" + ry1 + " ry2=" + ry2);

        // Draw bounding box rectangle
        g2d.setColor(Color.BLUE);
        g2d.drawRect(1, 1, (int) getWidth()-2, getHeight()-2);

        //g2d.setColor(Color.GREEN);
        //g2d.drawRect(3, getHeight()-3-100, 100, 100);

	g2d.setStroke(oldStroke);
    }
}

public class Main {
    public static void main(String[] args) {
        List<Point> points = readPointsFromFile("rangepoints.txt");
        RangeTree rangeTree = new RangeTree(points);

        // Example range query
        double x1 = 1.0, y1 = 1.0, x2 = 50.0, y2 = 50.0;

        // Create Swing window
        JFrame frame = new JFrame("Range Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        // Create drawing panel
        DrawingPanel drawingPanel = new DrawingPanel(points, rangeTree.minX, rangeTree.minY, rangeTree.maxX, rangeTree.maxY);
        drawingPanel.x1 = x1;
        drawingPanel.y1 = y1;
        drawingPanel.x2 = x2;
        drawingPanel.y2 = y2;

        // Add drawing panel to the frame
        frame.add(drawingPanel);

        // Show the frame
        frame.setVisible(true);

        List<Point> result = rangeTree.queryRange(x1, y1, x2, y2);
        System.out.println("Points within the range [" + x1 + ", " + y1 + "] to [" + x2 + ", " + y2 + "]:");
        for (Point point : result) {
            System.out.println("(" + point.x + ", " + point.y + ")");
        }

    }

    private static List<Point> readPointsFromFile(String filename) {
        List<Point> points = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(filename));
            while (scanner.hasNextLine()) {
                String[] coordinates = scanner.nextLine().split(",");
                double x = Double.parseDouble(coordinates[0]);
                double y = Double.parseDouble(coordinates[1]);
                points.add(new Point(x, y));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return points;
    }
}
