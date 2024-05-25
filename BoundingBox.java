public class BoundingBox {
    double minX;
    double maxX;
    double minY;
    double maxY;
    public BoundingBox() {
	this.minX = 0;
	this.maxX = 0;
	this.minY = 0;
	this.maxY = 0;
    }
    public BoundingBox(double minX, double maxX, double minY, double maxY) {
	this.minX = minX;
	this.maxX = maxX;
	this.minY = minY;
	this.maxY = maxY;
    }
    public boolean contains(Point point) {
        return this.minX <= point.x && point.x <= this.maxX &&
        this.minY <= point.y && point.y <= this.maxY;
    }

}
