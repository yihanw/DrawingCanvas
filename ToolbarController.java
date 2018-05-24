import java.awt.event.*;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class ToolbarController implements ActionListener, ChangeListener {
	DrawingModel model;
	ToolbarController(DrawingModel model) {
		this.model = model;
	}
	
	// delete button
	public void actionPerformed(ActionEvent e){
		String name = e.toString();
		if (name.contains("JMenuItem")){
			//System.out.println(((JMenuItem)e.getSource()).getText());
			model.changeColour(((JMenuItem)e.getSource()).getText());
		} else {
		//System.out.println(name);
		//System.out.println("toolbar controller: delete button");
		//System.out.println(e.toString());
			model.deleteStroke();
		}
	}
	
	// slider
	public void stateChanged(ChangeEvent e) {
		JSlider s = (JSlider)e.getSource();
		if (s.getName() == "Scale"){
			float value = s.getValue()/100f;
			model.setScaleSlider(value);
			model.updateScale();
		} else if (s.getName() == "Rotate"){
			int value = s.getValue();
			model.setRotateSlider(value);
			model.updateRotate();
		}
	}

}