public class Node {
    // vlasenko@:
    // no need: we choose point which has leftMax value becase list is sorted
    // so leftMax is stored in Point by construction,
    /*
    public double leftMax;
    public double getLeftMax() {
	return leftMax;
    };

    public void setLeftMax(double value) {
	leftMax = value;
    };
    */

    public Node left, right;
    public RangeTree sub_tree = null;
    public Point point;

    public Node(Point point) {
        this.point = point;
    }

    public boolean isLeaf() {
        if (left == null && right == null) {
            return true;
        }
        return false;
    };

}
