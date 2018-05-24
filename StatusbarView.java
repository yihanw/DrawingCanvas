import java.awt.Color;

import javax.swing.*;

class StatusbarView extends JPanel implements IView {
	// the model that this view is showing

	private DrawingModel model;
	JLabel statusLabel = new JLabel();
	
	StatusbarView(DrawingModel model, ToolbarController toolbarController) {
		super.setBackground(Color.WHITE);
		// set the model
		this.model = model;
		this.add(this.statusLabel);
	}
	
	// IView interface
	public void updateView() {
		String txt = this.model.getStatusText();
		statusLabel.setText(txt);
	}
}
