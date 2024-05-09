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
    public Node root;
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
    private RangeTree rangeTree;
    // BoundingBox for range query
    public double x1, y1, x2, y2;
    // BoundingBox for the set of points
    private double minX, minY, maxX, maxY;
    // coefficients from point coordinates to screen
    public double coefficientX, coefficientY;

    public DrawingPanel(RangeTree rtree, double x1, double y1, double x2, double y2) {
	this.rangeTree = rtree;
	this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.minX = rangeTree.minX;
        this.minY = rangeTree.minY;
        this.maxX = rangeTree.maxX;
        this.maxY = rangeTree.maxY;

	int screenWidth = 600;
	int screenHeight = 600;
        setPreferredSize(new Dimension(screenWidth, screenHeight));
	double pointsWidth = maxX - minX;
	double pointsHeight = maxY - minY;
	// to screen pixel coordinates
	this.coefficientX = (double) screenWidth / pointsWidth;
	this.coefficientY = (double) screenHeight/ pointsHeight;
    }

    public int panelX( double x) {
	return (int) ((x - minX) * coefficientX);
    }
    public int panelY( double y) {
        return (int) ((maxY - y) * coefficientY);
    }



    /**
     * Draw an arrow line between two points.
     * @param g the graphics component.
     * @param x1 x-position of first point.
     * @param y1 y-position of first point.
     * @param x2 x-position of second point.
     * @param y2 y-position of second point.
     * @param d  the width of the arrow.
     * @param h  the height of the arrow.
     */
    private void drawArrowLine(Graphics2D g, int x1, int y1, int x2, int y2, int d, int h) {
	int dx = x2 - x1, dy = y2 - y1;
	double D = Math.sqrt(dx*dx + dy*dy);
	double xm = D - d, xn = xm, ym = h, yn = -h, x;
	double sin = dy / D, cos = dx / D;

	x = xm*cos - ym*sin + x1;
	ym = xm*sin + ym*cos + y1;
	xm = x;

	x = xn*cos - yn*sin + x1;
	yn = xn*sin + yn*cos + y1;
	xn = x;

	int[] xpoints = {x2, (int) xm, (int) xn};
	int[] ypoints = {y2, (int) ym, (int) yn};

	g.drawLine(x1, y1, x2, y2);
	g.fillPolygon(xpoints, ypoints, 3);
    }


    private void drawNode(Graphics2D g2d, Node node) {
        if (node == null)
            return;

        Point point = node.point;
	g2d.setColor(Color.BLACK);
	g2d.fillOval(panelX(point.x) - 3, panelY(point.y) - 3, 6, 6);

	Node leftNode = node.left;
	if (leftNode != null) {
	    Point headpoint = leftNode.point;
	    g2d.setColor(Color.GREEN);
	    drawArrowLine(g2d,
			  panelX(point.x), panelY(point.y),
			  panelX(headpoint.x), panelY(headpoint.y),
			  7, 7);
	    drawNode(g2d, leftNode);
	}
	Node rightNode = node.right;
	if (rightNode != null) {
	    Point headpoint = rightNode.point;
	    g2d.setColor(Color.YELLOW);
	    drawArrowLine(g2d,
			  panelX(point.x), panelY(point.y),
			  panelX(headpoint.x), panelY(headpoint.y),
			  7, 7);
	    drawNode(g2d, rightNode);
	}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw points
	drawNode(g2d, rangeTree.root);

	float thickness = 3;
	Stroke oldStroke = g2d.getStroke();
	g2d.setStroke(new BasicStroke(thickness));

        // Draw bounding box rectangle
        g2d.setColor(Color.BLUE);
        g2d.drawRect(1, 1, (int) getWidth()-2, getHeight()-2);
        // Draw range query rectangle
        int rx1 = panelX(x1);
	int rx2 = panelX(x2);
        int ry1 = panelY(y1);
	int ry2 = panelY(y2);
        g2d.setColor(Color.RED);
        g2d.drawRect(rx1, ry2, rx2 - rx1, ry1 - ry2);
	//System.out.println("x1=" + x1 + " x2=" + x2 + " y1=" + y1 + " y2=" + y2);
	//System.out.println("rx1=" + rx1 + " rx2=" + rx2 + " ry1=" + ry1 + " ry2=" + ry2);



	g2d.setStroke(oldStroke);
    }
}

public class Main {
    public static void main(String[] args) {
        List<Point> points = readPointsFromFile("rangepoints.txt");
        RangeTree rangeTree = new RangeTree(points);

        // Example range query
        double x1 = 1.0, y1 = 1.0, x2 = 50.0, y2 = 50.0;

        List<Point> result = rangeTree.queryRange(x1, y1, x2, y2);
        System.out.println("Points within the range [" + x1 + ", " + y1 + "] to [" + x2 + ", " + y2 + "]:");
        for (Point point : result) {
            System.out.println("(" + point.x + ", " + point.y + ")");
        }

        // Create Swing window
        JFrame frame = new JFrame("Range Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        // Create drawing panel
        DrawingPanel drawingPanel = new DrawingPanel(rangeTree, x1, y1, x2, y2);

        // Add drawing panel to the frame
        frame.add(drawingPanel);

        // Show the frame
        frame.setVisible(true);

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
