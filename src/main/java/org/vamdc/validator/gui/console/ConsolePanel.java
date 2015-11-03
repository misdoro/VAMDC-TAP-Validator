package org.vamdc.validator.gui.console;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.PositionMemoryDialog;

public class ConsolePanel extends PositionMemoryDialog{

	private static final long serialVersionUID = 5729446011617248198L;

	private JTextArea text = new JTextArea();

	public ConsolePanel(Frame owner){
		super(owner,"Log console",Setting.GUILogConsoleDim);

		initStreams();
		if (Setting.GUILogConsole.getBool())
			this.setVisible(true);
	}



	private void initStreams() {
		System.out.println("Switching output to GUI Console (ctrl-t to show)");
		PrintStream printer = new PrintStream(new TextPrinter(text),true);
		System.setOut(printer);
		System.setErr(printer);
		this.clear();
	}

	protected JPanel lazyInitLayout(){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		text.setEditable(false);
		panel.add(new JScrollPane(text));
		
		//panel.setPreferredSize(new Dimension(640,480));

		initCloseEvent();
		
		return panel;
	}

	private static class TextPrinter extends FilterOutputStream{
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
	}

	protected void initCloseEvent() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Setting.GUILogConsole.saveValue(Boolean.FALSE);
			}
		});
	}
	



}
