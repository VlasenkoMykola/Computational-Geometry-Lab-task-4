import java.util.Arrays;
import java.util.TreeMap;
import java.util.Map;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

public class RangeTree {
    public Node root;
    private Axis by;

    public RangeTree() {
        this.by = Axis.X;
    }
    public RangeTree(Axis by) {
        this.by = by;
    }

    public RangeTree(Point[] points) {
        this.by = Axis.X;
	buildBinaryTree(points);
    }
    public RangeTree(ArrayList<Point> points) {
        this.by = Axis.X;
	buildBinaryTree(points.toArray(new Point[0]));
    }

    public void buildBinaryTree(Point[] points) {
        if (points.length == 0) {
            this.root = null;
        }
        else if (points.length == 1) {
	    this.root = this.sortedList2BinaryTree(points);
	}
	else {
	    Point[] sorted = Sort.sort(points, this.by);
	    this.root = this.sortedList2BinaryTree(sorted);
	}
    }

    public void setRoot(Node node) {
	this.root = node;
    }

    private Node sortedList2BinaryTree(Point[] points) {
        if (points.length == 0) {
            return null;
        }
        else if (points.length == 1) {
            Node root = new Node(points[0]);
	    if (this.by == Axis.X) {
		root.sub_tree = new RangeTree(Axis.Y);
		root.sub_tree.setRoot(new Node(points[0]));
	    }
            return root;
        }
        else {
            int med = points.length/2;
            Point point = points[med];
            Point[] p1 = Arrays.copyOfRange(points, 0, med);
            Point[] p2 = Arrays.copyOfRange(points, med+1, points.length);

            Node root = new Node(point);

            Node t1 = sortedList2BinaryTree(p1);
            Node t2 = sortedList2BinaryTree(p2);

            root.left = t1;
            root.right = t2;
	    if (this.by == Axis.X) {
		root.sub_tree = new RangeTree(Axis.Y);
		root.sub_tree.buildBinaryTree(points);
	    }
            return root;
        }
    }

    private boolean is_leaf(Node root) {
        if (root == null || (root.left == null && root.right == null)) {
            return true;
        }
        return false;
    }

    private Node findMaxNodeX(Node root, BoundingBox range) {
	while (!(this.is_leaf(root)) && (root.point.x > range.maxX || root.point.x < range.minX)) {
	    if (root.point.x >= range.maxX) {
		root = root.left;
	    }
	    else {
		root = root.right;
	    }
	}
	return root;
    }

    private Node findMaxNodeY(Node root, BoundingBox range) {
	while (!(this.is_leaf(root)) && (root.point.y > range.maxY || root.point.y < range.minY)) {
	    if (root.point.y >= range.maxY) {
		root = root.left;
	    }
	    else {
		root = root.right;
	    }
	}
	return root;
    }


    public List<Point> rangeQuery(BoundingBox range) {

	List<Point> results = new ArrayList<Point>();

        Node u = this.findMaxNodeX(this.root, range);

        if (u != null) {

            if (range.contains(u.point)) {
                results.add(u.point);
            }

            Node v = u.left;
            while (v != null) {
                if (range.contains(v.point)) {
		    results.add(v.point);
                }

                if (range.minX <= v.point.x) {

                    if (v.right != null)  {
			v.right.sub_tree.queryYRange(range, results);
                    }

                    v = v.left;
                } else {
                    v = v.right;
                }
            }

            v = u.right;
            while (v != null) {
                if (range.contains(v.point)) {
		    results.add(v.point);
                }

                if (range.maxX >= v.point.x) {

                    if (v.left != null)  {
			v.left.sub_tree.queryYRange(range, results);
                    }

                    v = v.right;
                } else {
                    v = v.left;
                }
            }
        }

	return results;
    }


    public void addTree(Node root, List<Point> results) {
	results.add(root.point);
	if (root.left != null)  {
	    addTree(root.left, results);
	}
	if (root.right != null)  {
	    addTree(root.right, results);
	}
    }

    public void queryYRange(BoundingBox range, List<Point> results) {

        Node u = this.findMaxNodeY(this.root, range);

        if (u != null) {

            if (range.contains(u.point)) {
		results.add(u.point);
            }

            Node v = u.left;
            while (v != null) {
                if (range.contains(v.point)) {
		    results.add(v.point);
                }

                if (range.minY <= v.point.y) {

                    if (v.right != null)  {
			addTree(v.right, results);
                    }

                    v = v.left;
                } else {
                    v = v.right;
                }
            }

            v = u.right;
            while (v != null) {
                if (range.contains(v.point)) {
		    results.add(v.point);
                }

                if (range.maxY >= v.point.y) {
                    if (v.left != null)  {
			addTree(v.left, results);
                    }

                    v = v.right;
                } else {
                    v = v.left;
                }
            }
        }
    }


    public BoundingBox getBoundingBox() {
	if (root == null) {
	    return null; // Empty tree has no bounding box
	}
	return calculateBoundingBox(root);
    }

    private BoundingBox calculateBoundingBox(Node node) {
	if (node.left == null && node.right == null) {
	    // Leaf node, bounding box is the point itself
	    return new BoundingBox(node.point.x, node.point.x, node.point.y, node.point.y);
	}

	BoundingBox leftBox = node.left != null ? calculateBoundingBox(node.left) : null;
	BoundingBox rightBox = node.right != null ? calculateBoundingBox(node.right) : null;

	double minX = node.point.x;
	double maxX = node.point.x;
	double minY = node.point.y;
	double maxY = node.point.y;

	if (leftBox != null) {
	    minX = Math.min(minX, leftBox.minX);
	    maxX = Math.max(maxX, leftBox.maxX);
	    minY = Math.min(minY, leftBox.minY);
	    maxY = Math.max(maxY, leftBox.maxY);
	}

	if (rightBox != null) {
	    minX = Math.min(minX, rightBox.minX);
	    maxX = Math.max(maxX, rightBox.maxX);
	    minY = Math.min(minY, rightBox.minY);
	    maxY = Math.max(maxY, rightBox.maxY);
	}

	return new BoundingBox(minX, maxX, minY, maxY);
  }


}


class Sort {
    public static Point[] sort(Point[] points, Axis by) {
        if (by == Axis.X) {
            Comparator<Point> by_X = (Point p1, Point p2) -> {
                double temp = p1.x - p2.x;
                if (temp > 0)   return 1;
                else if (temp < 0)  return -1;
                return 0;
            };
            Arrays.sort(points, by_X);
            return points;
        } else {
            Comparator<Point> by_Y = (Point p1, Point p2) -> {
                double temp = p1.y - p2.y;
                if (temp > 0)   return 1;
                else if (temp < 0)  return -1;
                return 0;
            };
            Arrays.sort(points, by_Y);
            return points;
        }
    }
}

enum Axis {

    X ("x"),
    Y ("y");

    private String label;

    Axis(String label) {
	this.label = label;
    }

    @Override
    public String toString() {
	return label;
    }
}
