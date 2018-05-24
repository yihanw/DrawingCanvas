import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

class ToolbarView extends JPanel implements IView {
	// the view's main user interface
	private JButton button;
	private JLabel scaleLabel;
	private JSlider scaleSlider;
	private JLabel scaleValue;
	private JLabel rotateLabel;
	private JSlider rotateSlider;
	private JLabel rotateValue;
	private JMenu menu = new JMenu("colours");
	
	// the model that this view is showing
	private DrawingModel model;
	
	ToolbarView(DrawingModel model, ToolbarController toolbarController) {
		super.setBackground(Color.WHITE);
		//delete
		button = new JButton("Delete");
		button.setText("Delete");
	
		// scale
		scaleLabel = new JLabel("Scale");	
		scaleSlider = new JSlider(50, 200, 100);	
		scaleSlider.setName("Scale");
		scaleValue = new JLabel("1.0");
		
		// Rotate
		rotateLabel = new JLabel("Rotate");		
		rotateSlider = new JSlider(-180, 180, 0);
		rotateSlider.setName("Rotate");
		rotateValue = new JLabel("0");
		
		// color
		for(String s: new String[] {"black", "red", "orange", "green", "blue"}){
			JMenuItem item = new JMenuItem(s);
			item.addActionListener(toolbarController);
			menu.add(item);
		}
		JMenuBar menubar = new JMenuBar();
		menubar.setName("menubar");
		menubar.add(menu);
		
		this.add(button);
		this.add(scaleLabel);
		this.add(scaleSlider);
		this.add(scaleValue);
		this.add(rotateLabel);
		this.add(rotateSlider);
		this.add(rotateValue);
		this.add(menubar);
		
		// set the model
		this.model = model;
		// setup the event to go to the controller
		button.addActionListener(toolbarController);
		scaleSlider.addChangeListener(toolbarController);
		rotateSlider.addChangeListener(toolbarController);
		menu.addActionListener(toolbarController);
		
	}
	
	// IView interface
	public void updateView() {
		Component[] components = super.getComponents();
		for(int i = 0; i < components.length; i++) {
			components[i].setEnabled(model.getEnableToolbar());
			//System.out.println(components[i].getName());
			if(components[i].getName() == "menubar"){
				Component[] menuComponents = ((JMenuBar) components[i]).getComponents();
				for (int j=0; j< menuComponents.length; j++){
					menuComponents[j].setEnabled(model.getEnableToolbar());
				}
			}
		}
		
		double sValue = this.model.getScaleValue();
		int rValue = this.model.getRotateValue();
		//System.out.println(rValue);
		//System.out.println("view: "+sValue);
		scaleSlider.setValue((int)(sValue * 100));
		rotateSlider.setValue(rValue);
		//System.out.println(rValue);
		scaleValue.setText(String.format("%.1f", sValue));
		rotateValue.setText("" + this.model.getRotateValue());
	}
}
