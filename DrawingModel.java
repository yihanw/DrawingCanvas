import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.vecmath.Point2d;

// View interface
interface IView {
	public void updateView();
}

public class DrawingModel extends JPanel{	
	// the data in the model, just a counter
	private float scaleValue = 1.0f;
	private int rotateValue = 0;
	private ArrayList<Shape> shapeList = new ArrayList<Shape>();
	private String statusText = "0 Strokes";
	private int strokeCount;
	private int hitIndex;
	private ArrayList<Integer> hitHistroy = new ArrayList<Integer>();
	private ArrayList<Integer> curHitList = new ArrayList<Integer>();
	private int highlightIndex;
	private Shape highlightShape = new Shape();
	private boolean enableToolbar = false;
	private Point2d newMove = new Point2d();
	private Point2d lastMove = new Point2d();
	
	// all views of this model
	private ArrayList<IView> views = new ArrayList<IView>();
	
	// set the view observer
	public void addView(IView view) {
		views.add(view);
		view.updateView();
	}
	
	// toolbar
	public void setEnableToolbar(boolean input){
		enableToolbar = input;
	}
	
	public boolean getEnableToolbar(){
		return enableToolbar;
	}
	
	// scale slider
	public double getScaleValue(){
		return this.scaleValue;
	}
	
	public void setScaleSlider(float value){
		//System.out.println("model: update scale slider");
		this.scaleValue = value;
		notifyObservers();
	}

	public void updateScale(){
		if (highlightIndex >= 0){
		shapeList.get(highlightIndex).scale = scaleValue;
		
		highlightShape.scale = scaleValue;
		int npoints = shapeList.get(highlightIndex).npoints();
		if (strokeCount == 1){
			statusText = strokeCount + " Stroke, Selection(" ;
		} else {
			statusText = strokeCount + " Strokes, Selection(" ;
		}
		statusText += npoints + " points, scale: " + 
				 scaleValue + ", rotation: " + rotateValue + ")";
		}
		notifyObservers();
	}
	
	// rotate slider
	public int getRotateValue(){
		return this.rotateValue;
	}
	
	public void setRotateSlider(int value){
		this.rotateValue = value;
		notifyObservers();
	}
	
	public void updateRotate(){
		if(highlightIndex >= 0){
		shapeList.get(highlightIndex).rotate = rotateValue;
		highlightShape.rotate = rotateValue;
		int npoints = shapeList.get(highlightIndex).npoints();
		if (strokeCount == 1){
			statusText = strokeCount + " Stroke, Selection(";
		} else {
			statusText = strokeCount + " Strokes, Selection(";
		}
		statusText += npoints + " points, scale: " + 
				 scaleValue + ", rotation: " + rotateValue + ")";
		}
		notifyObservers();
	}
	
	// status bar
	public String getStatusText(){
		return statusText;
	}
	
	// canvas
	public void pressed(double x, double y){
		// create new shape
		deleteEmptyShape();
		lastMove = new Point2d(x,y);
		
		if (strokeCount == 1){
			statusText = strokeCount + " Stroke";
		} else {
			statusText = strokeCount + " Strokes";
		}
		Shape shape = new Shape();	
        shape.scale = 1.0f;
        shapeList.add(shape);
        
        for(int i=0; i<shapeList.size(); i++){
        	shapeList.get(i).newMove = new Point2d(0,0);
        }

        notifyObservers();
	}
	
	public void dragged(double x, double y){	
		// drag new shape
		if (curHitList.isEmpty()){
			shapeList.get(shapeList.size()-1).addPoint(x, y);
			strokeCount = shapeList.size();
			if (strokeCount == 1){
				statusText = strokeCount + " Stroke";
			} else {
				statusText = strokeCount + " Strokes";
			}
			updateHitHistroy(strokeCount-1);
		} else { // move highlighted stroke

			//System.out.println("here");
			newMove = new Point2d(x,y);
			shapeList.get(highlightIndex).newMove = new Point2d(newMove.x-lastMove.x, newMove.y-lastMove.y);
			lastMove = newMove;
		}
		notifyObservers();
	}
	
	public int getShapeListSize(){
		return shapeList.size();
	}
	
	public Shape getShape(int i){
		if (shapeList.size() > 0){
			return shapeList.get(i);
		} 
		return null;
	}
	
	public int getHitIndex(){
		return hitIndex;
	}
	
	public void deleteEmptyShape(){
		for (int i=0; i<shapeList.size(); i++){
			if (shapeList.get(i).npoints() == 0){
				shapeList.remove(i);
			}
		}
	}
	
	public int getStrokeCount(){
		return strokeCount;
	}
	
	public void clicked(int x, int y){
		enableToolbar = true;
		
				highlightIndex = -1;
				
				scaleValue = 1.0f;
				rotateValue = 0;
				highlightShape.setColour(Color.WHITE);
				highlightShape.clearPoints();
				curHitList.clear();
				
				//find curHitlist
				for (int i=0; i<shapeList.size(); i++){
					Shape curShape = shapeList.get(i);
					if (curShape.hittest(x, y) == true){
						curHitList.add(i);
						highlightIndex = i;
					}
				}
				
				if (curHitList.size()>0){
					highlightIndex = findHighlightIndex();
				}
				
				if (highlightIndex >= 0){
					
					Shape curShape = shapeList.get(highlightIndex);
					scaleValue = curShape.scale;
					rotateValue = curShape.rotate;
					highlightShape.scale = curShape.scale;
					highlightShape.rotate = curShape.rotate;
					highlightShape.totalMove = curShape.totalMove;
					highlight(highlightIndex);
					updateHitHistroy(highlightIndex);
					enableToolbar = true;
					//System.out.println("click: "+highlightIndex);
					int npoints = curShape.npoints();
					if (strokeCount == 1){
						statusText = strokeCount + " Stroke, Selection(";
					} else {
						statusText = strokeCount + " Strokes, Selection(";
					}
					
					statusText += npoints + " points, scale: " + 
								 scaleValue + ", rotation: " + rotateValue + ")";
								 
				} else {
					enableToolbar = false;
				}
				notifyObservers();
	}
	
	public void highlight(int i){
		Shape curShape = shapeList.get(i);
		for(int j=0; j<curShape.npoints(); j++){
			highlightShape.addPoint(curShape.getPoints().get(j));
		}
		highlightShape.setColour(Color.YELLOW);
		highlightShape.setStrokeThickness(6.0f);
	}
	
	public void updateHitHistroy(int i){
		if (hitHistroy.contains(i)){
			int index = hitHistroy.indexOf(i);
			hitHistroy.remove(index);
		}
		hitHistroy.add(0, i);
	}
	
	public Shape getHighlightShape(){
		return highlightShape;
	}
	
	public int findHighlightIndex(){
		if (curHitList.size()==1){
			return curHitList.get(0);
		} else{
			int smallestIndex = curHitList.get(curHitList.size()-1);
			for(int i=0; i<curHitList.size();i++){
				if (hitHistroy.indexOf(curHitList.get(i)) < smallestIndex){
					smallestIndex = hitHistroy.indexOf(curHitList.get(i));
				}
			}
			
			return hitHistroy.get(smallestIndex);
		}
	}
	
	public void deleteStroke(){
		highlightShape.setColour(Color.WHITE);
		highlightShape.clearPoints();
		shapeList.remove(highlightIndex);
		
		int index = hitHistroy.indexOf(highlightIndex);
		//System.out.println(index);
		hitHistroy.remove(index);
		for (int i=0; i<hitHistroy.size(); i++){
			if (hitHistroy.get(i)>highlightIndex){
				hitHistroy.set(i, hitHistroy.get(i)-1);
			}
		}
		strokeCount--;
		if (strokeCount == 1){
			statusText = strokeCount + " Stroke";
		} else {
			statusText = strokeCount + " Strokes";
		}
		enableToolbar = false;
		notifyObservers();
	}
	
	public Point2d findPreferredSize(){
		double maxX=0, maxY=0;
		for(int i=0; i<shapeList.size(); i++){
			if (shapeList.get(i).findMaxPoint().x > maxX){
    			maxX = shapeList.get(i).findMaxPoint().x;
    		}
			if (shapeList.get(i).findMaxPoint().y > maxY){
    			maxY = shapeList.get(i).findMaxPoint().y;
    		}
		}
		return new Point2d(maxX, maxY);
	}
	
	public void changeColour(String colourName){
		if(highlightIndex >= 0){
			switch(colourName){
			case "black":
				shapeList.get(highlightIndex).colour = Color.BLACK;
				break;
			case "red":
				shapeList.get(highlightIndex).colour = Color.RED;
				break;
			case "orange":
				shapeList.get(highlightIndex).colour = Color.ORANGE;
				break;
			case "green":
				shapeList.get(highlightIndex).colour = Color.GREEN;
				break;
			case "blue":
				shapeList.get(highlightIndex).colour = Color.BLUE;
				break;
			}
		}
		notifyObservers();
	}
	
	// notify the IView observer
	private void notifyObservers() {
		for (IView view : this.views) {
			//System.out.println("Model: notify View");
			view.updateView();
		}
	}
}

