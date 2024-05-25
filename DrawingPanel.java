import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;

class DrawingPanel extends JPanel {
    private RangeTree rangeTree;
    // BoundingBox for range query
    BoundingBox query;
    // BoundingBox for the set of points
    private double minX, minY, maxX, maxY;
    // coefficients from point coordinates to screen
    public double coefficientX, coefficientY;

    public DrawingPanel(RangeTree rtree, BoundingBox query) {
	this.rangeTree = rtree;
	this.query = query;
	BoundingBox bb = rangeTree.getBoundingBox();
        this.minX = bb.minX;
        this.minY = bb.minY;
        this.maxX = bb.maxX;
        this.maxY = bb.maxY;

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

    // returns mean value point of the previous hierarchy
    private Point drawNode(Graphics2D g2d, Node node, Axis axis) {
        if (node == null)
            return null;

	if (node.isLeaf()) {
	    Point point = node.point;
	    g2d.setColor(Color.BLACK);
	    g2d.fillOval(panelX(point.x) - 3, panelY(point.y) - 3, 6, 6);
	    return point;
	}

	if (node.sub_tree != null) {
	    drawNode(g2d, node.sub_tree.root, node.sub_tree.axis);
	}
	Node leftNode = node.left;
	Point leftPoint = drawNode(g2d, leftNode, axis);
	Node rightNode = node.right;
	Point rightPoint = drawNode(g2d, rightNode, axis);
	// should not happen
	if (leftPoint == null) {
	    return rightPoint;
	}
	// should not happen
	if (rightPoint == null) {
	    return leftPoint;
	}
	Point centerPoint = new Point((leftPoint.y + rightPoint.y)/2, (leftPoint.y + rightPoint.y)/2);
	g2d.setColor(axis == Axis.X ? Color.GREEN : Color.YELLOW);
	if (leftPoint != null) {
	    drawArrowLine(g2d,
			  panelX(centerPoint.x), panelY(centerPoint.y),
			  panelX(leftPoint.x), panelY(leftPoint.y),
			  7, 7);
	}
	if (rightPoint != null) {
	    drawArrowLine(g2d,
			  panelX(centerPoint.x), panelY(centerPoint.y),
			  panelX(rightPoint.x), panelY(rightPoint.y),
			  7, 7);
	}
	return centerPoint;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

	float thickness = 3;
	Stroke oldStroke = g2d.getStroke();
	g2d.setStroke(new BasicStroke(thickness));

        // Draw bounding box rectangle
        g2d.setColor(Color.BLUE);
        g2d.drawRect(1, 1, (int) getWidth()-2, getHeight()-2);
        // Draw range query rectangle
        int rx1 = panelX(query.minX);
	int rx2 = panelX(query.maxX);
        int ry1 = panelY(query.minY);
	int ry2 = panelY(query.maxY);
        g2d.setColor(Color.RED);
        g2d.drawRect(rx1, ry2, rx2 - rx1, ry1 - ry2);
	//System.out.println("x1=" + x1 + " x2=" + x2 + " y1=" + y1 + " y2=" + y2);
	//System.out.println("rx1=" + rx1 + " rx2=" + rx2 + " ry1=" + ry1 + " ry2=" + ry2);
	g2d.setStroke(oldStroke);

        // Draw points
	drawNode(g2d, rangeTree.root, rangeTree.axis);

    }
}
