package org.vamdc.validator.gui.console;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.vamdc.validator.Setting;

public class ConsolePanel extends JDialog{

	private static final long serialVersionUID = 5729446011617248198L;

	private JTextArea text = new JTextArea();
	private JPanel panel = new JPanel();

	public ConsolePanel(Frame owner){
		super(owner,"Log console");
		initDialog();
		initCloseEvent();
		initLayout();
		initStreams();
		loadDimensions();
	}
	
	public void saveDimensions(){
		Rectangle pos = this.getBounds();
		Setting.GUILogConsoleDim.saveObject(pos);
	}
	
	public void loadDimensions(){
		Object pos = Setting.GUILogConsoleDim.loadObject();
		if (pos instanceof Rectangle){
			this.setBounds((Rectangle) pos);
		}else{
			this.pack();
		}
	}

	private void initCloseEvent() {
		this.addWindowListener(
				new WindowAdapter(){
					@Override
					public void windowClosed(WindowEvent e){Setting.GUILogConsole.saveValue(false);}
					@Override
					public void windowClosing(WindowEvent e){
						Setting.GUILogConsole.saveValue(false);
						saveDimensions();
					}
					@Override
					public void windowLostFocus(WindowEvent e) {
						saveDimensions();
					}
				}
				);
	}

	private void initDialog() {
		this.setContentPane(panel);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setModal(false);
	}

	private void initStreams() {
		PrintStream printer = new PrintStream(new TextPrinter(text),true);
		System.setOut(printer);
		System.setErr(printer);
		this.clear();
	}

	private void initLayout(){
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));

		JScrollPane scroll = new JScrollPane(text);

		panel.add(scroll);
		panel.setPreferredSize(new Dimension(640,480));
	}

	private class TextPrinter extends FilterOutputStream{
		private JTextArea textArea;

		TextPrinter(JTextArea text){
			super(new ByteArrayOutputStream());
			this.textArea = text;
		}

		@Override
		public void flush() throws IOException {
			super.flush();
			String text = out.toString();
			out = new ByteArrayOutputStream();
			textArea.append(text);
		}

	}

	public void clear(){
		text.setText("");
		System.out.println("Application log console");
	}



}
