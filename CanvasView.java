import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Dimension;

class CanvasView extends JPanel implements IView {
	// the model that this view is showing
	private DrawingModel model;
	
	CanvasView(DrawingModel model, CanvasController canvasController) {
		setBackground(Color.WHITE);
		this.model = model;
		super.addMouseListener(canvasController);
		super.addMouseMotionListener(canvasController);
	}
	

	public void paintComponent(Graphics g) {
		//System.out.println("paint component");
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        Shape highlight = model.getHighlightShape();
        if (highlight != null){
        	highlight.draw(g2);
        }
        
        int size = model.getShapeListSize();
        for (int i=0; i< size; i++){
        	Shape curShape = model.getShape(i);
        	if (curShape != null){
        		curShape.draw(g2);
        	}
        }  
    }
	
	// IView interface
	public void updateView() {
		//System.out.println("CanvasView: updateView");
		int x = (int)model.findPreferredSize().x;
		int y = (int)model.findPreferredSize().y;
		super.setPreferredSize(new Dimension(x, y));
		revalidate();
		repaint();
	}
}
