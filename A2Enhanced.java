/*
 * Yihan Wang (20610851)
 * CS349, A2Enhanced
 * 
 * Add a colour pallette on toolbar, a user change the colour of a selected stroke,
 * the colour paallette is disabled when no stokes is selected
 */

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class A2Enhanced {

	public static void main(String[] args){	
		JFrame frame = new JFrame("A2Enhanced");
		
		DrawingModel model = new DrawingModel();
		
		// toolbar
		ToolbarController toolbarController = new ToolbarController(model);
		ToolbarView toolbarView = new ToolbarView(model, toolbarController);
		model.addView(toolbarView);
		
		// canvas
		CanvasController canvasController = new CanvasController(model);
		CanvasView canvasView = new CanvasView(model, canvasController);
		model.addView(canvasView);
		
		// statusbar
		StatusbarView statusView = new StatusbarView(model, toolbarController);
		model.addView(statusView);
		
		// create the window
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.setPreferredSize(new Dimension(800,50));
		p1.add(toolbarView);
		p1.setBackground(Color.WHITE);
		
		//JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		//p2.setBackground(Color.WHITE);
		//p2.add(canvasView);

		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.setViewportView(canvasView);
		
		JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p3.setPreferredSize(new Dimension(800,50));
		p3.add(statusView);
		p3.setBackground(Color.WHITE);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setPreferredSize(new Dimension(800,600));
		//frame.setBackground(Color.BLUE);
		
		frame.getContentPane().add(p1, BorderLayout.PAGE_START);
		frame.getContentPane().add(scrollPanel, BorderLayout.CENTER);
        //frame.getContentPane().add(p2, BorderLayout.CENTER);
        frame.getContentPane().add(p3, BorderLayout.PAGE_END);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
