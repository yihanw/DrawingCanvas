import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class CanvasController extends MouseAdapter {
	DrawingModel model;
	
	CanvasController(DrawingModel model) {
		this.model = model;
	}
	
	public void mousePressed(MouseEvent e){
		model.pressed(e.getX(), e.getY());
	}
	
	public void mouseClicked(MouseEvent e){
		model.clicked(e.getX(), e.getY());
	}
	
	public void mouseDragged(MouseEvent e) {
		model.dragged(e.getX(), e.getY());
    }
}
