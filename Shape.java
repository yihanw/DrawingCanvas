import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.vecmath.*;

//import com.sun.media.sound.ModelAbstractChannelMixer;

// simple shape model class
class Shape {
    // shape points
    ArrayList<Point2d> points = new ArrayList<Point2d>();

    public ArrayList<Point2d> getPoints(){
    	return points;
    }

    public boolean isContained(Point2d p){
    	
    	for (int i=0; i<points.size(); i++){
    	//	System.out.println(points.get(i) +" "+p);
    		double px = points.get(i).x;
        	double py = points.get(i).y;
        	if ((px>=p.x-5 && px<=p.x+5) && (py>=p.y-5 && py<=p.y+5)){
    			return true;
        		//System.out.println(p);
    		}
    	}
    	return false;
    }
    
    public void clearPoints() {
        points = new ArrayList<Point2d>();
        pointsChanged = true;
    }
  
    // add a point to end of shape
    public void addPoint(Point2d p) {
        if (points == null) clearPoints();
        points.add(p);
        pointsChanged = true;
    }    
    // add a point to end of shape
    public void addPoint(double x, double y) {
        addPoint(new Point2d(x, y));  
    }
    
    public int npoints() {
        return points.size();
    }
    
    // shape is polyline or polygon
    Boolean isClosed = false; 
    public Boolean getIsClosed() {
        return isClosed;
    }
    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }    
    // if polygon is filled or not
    Boolean isFilled = false; 
    public Boolean getIsFilled() {
        return isFilled;
    }
    public void setIsFilled(Boolean isFilled) {
        this.isFilled = isFilled;
    }    
    // drawing attributes
    Color colour = Color.BLACK;
    float strokeThickness = 2.0f;
    public Color getColour() {
		return colour;
	}
	public void setColour(Color colour) {
		this.colour = colour;
	}
    public float getStrokeThickness() {
		return strokeThickness;
	}
	public void setStrokeThickness(float strokeThickness) {
		this.strokeThickness = strokeThickness;
	}
    // shape's transform
    // quick hack, get and set would be better
    float scale = 1.0f;
    int rotate = 0;
    // some optimization to cache points for drawing
    Boolean pointsChanged = false; // dirty bit
    int[] xpoints, ypoints;
    int npoints = 0;
    void cachePointsArray() {
        xpoints = new int[points.size()];
        ypoints = new int[points.size()];
        for (int i=0; i < points.size(); i++) {
            xpoints[i] = (int)points.get(i).x;
            ypoints[i] = (int)points.get(i).y;
        }
        npoints = points.size();
        pointsChanged = false;
    }
    
    public Point2d findMaxPoint(){
    	double maxX=0, maxY=0;
    	double originX, originY, px, py;
    	for(int i=0; i<points.size(); i++){	
    		originX = points.get(i).x - center.x;
        	originY = points.get(i).y - center.y;
        	px = originX;
        	py = originY;
        	if (rotate != 0){
        		double ratateR = Math.toRadians(rotate);
        		px = originX * Math.cos(ratateR) - originY * Math.sin(ratateR);
				py = originX * Math.sin(ratateR) + originY* Math.cos(ratateR);
        	}
        	px = px * this.scale;
        	py = py * this.scale;
        	px = px + center.x;
        	py = py + center.y;
        	px = px + totalMove.x;// + totalMove.x;
        	py = py + totalMove.y;// + totalMove.y;

    		if (px > maxX){
    			maxX = px;
    		}
    		if (py > maxY){
    			maxY = py;
    		}
    	}
    	return new Point2d(maxX, maxY);
    }
    
    Point2d center = new Point2d();
    public void findCenter(){
    	int minX=800, maxX=0, minY=600, maxY=0;
    	for(int i=0; i<points.size(); i++){
    		double x =  points.get(i).x;
    		double y = points.get(i).y;
    		minX = (int) Math.min(x,  minX);
    		maxX = (int) Math.max(x,  maxX);
    		minY = (int) Math.min(y,  minY);
    		maxY = (int) Math.max(y,  maxY);
    	}
    	//g.drawRect(minX, minY, maxX-minX, maxY-minY);
    	center.x = (maxX-minX)/2+minX;
    	center.y = (maxY-minY)/2+minY;
    	//System.out.println(minX+" "+maxX+" "+minY+" "+maxY);
    }
    
    Point2d newMove = new Point2d();
    Point2d totalMove = new Point2d();
    
    public void calculateTotalMove(){
    	totalMove.x += newMove.x;
    	totalMove.y += newMove.y;
    	//System.out.println(totalMove);
    }
    
    // let the shape draw itself
    // (note this isn't good separation of shape View from shape Model)
    public void draw(Graphics2D g2) {
        // don't draw if points are empty (not shape)
        if (points == null) return;
        // see if we need to update the cache
        if (pointsChanged) cachePointsArray();
        // save the current g2 transform matrix 
        AffineTransform M = g2.getTransform();
        findCenter();
        calculateTotalMove();
        g2.translate(totalMove.x, totalMove.y);
        g2.translate((int)center.x, (int)center.y);
        g2.scale(scale, scale);
        g2.rotate(Math.toRadians(rotate));
        g2.translate(-(int)center.x, -(int)center.y);
        // call drawing functions
        g2.setColor(colour);            
        if (isFilled) {
            g2.fillPolygon(xpoints, ypoints, npoints);
        } else {
            // can adjust stroke size using scale
        	g2.setStroke(new BasicStroke(strokeThickness / scale)); 
        	if (isClosed)
                g2.drawPolygon(xpoints, ypoints, npoints);
            else
                g2.drawPolyline(xpoints, ypoints, npoints);
        }
        // reset the transform to what it was before we drew the shape
        g2.setTransform(M);            
    }
    
   
    // let shape handle its own hit testing
    // (x,y) is the point to test against
    // (x,y) needs to be in same coordinate frame as shape, you could add
    // a panel-to-shape transform as an extra parameter to this function
    // (note this isn't good separation of shape Controller from shape Model)    
    public boolean hittest(double x, double y)
    {   
    	//System.out.println("hit: "+x+" "+y);
    	//System.out.println("in hittest");
    	if (points != null) {
    		double px, py, originX, originY;
            for (int i=0; i<points.size(); i++){
            	//System.out.println("points: "+points.get(i).x+ " "+points.get(i).y);
            	originX = points.get(i).x - center.x;
            	originY = points.get(i).y - center.y;
            	px = originX;
            	py = originY;
            	if (rotate != 0){
            		double ratateR = Math.toRadians(rotate);
            		px = originX * Math.cos(ratateR) - originY * Math.sin(ratateR);
					py = originX * Math.sin(ratateR) + originY* Math.cos(ratateR);
            	}
            	px = px * this.scale;
            	py = py * this.scale;
            	px = px + center.x;
            	py = py + center.y;
            	px = px + totalMove.x;// + totalMove.x;
            	py = py + totalMove.y;// + totalMove.y;
            	// + center.y;
            	if ((px>=x-5 && px<=x+5) && (py>=y-5 && py<=y+5)){
            		//System.out.println("hitted");
            		return true;
            	}
            }
    	}
    	
    	return false;
    }
}
