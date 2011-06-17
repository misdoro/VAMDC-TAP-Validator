package org.vamdc.validator.gui.mainframe;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

/**
 * Menu is defined here.
 * @author doronin
 */
public class MenuBar extends JMenuBar{

	/**
	 * Commands definition
	 */
	public final static String CMD_OPEN		="MenuOpen";
	public final static String CMD_SAVE		="MenuSave";
	public final static String CMD_EXIT		="MenuExit";
	public final static String CMD_FIND		="MenuFind";
	public final static String CMD_FINDNEXT	="MenuFindNext";
	public final static String CMD_CONFIG	="MenuConfigure";
	public final static String CMD_ABOUT	="MenuAbout";
	public final static String CMD_USAGE	="MenuUsage";
	
	private static final long serialVersionUID = -6004839698908145L;
	
	private ActionListener controller = null;

	private JMenu		menuFile		=	new JMenu("File");
	private	JMenu		menuEdit		=	new JMenu("Edit");
	private	JMenu		menuSettings	=	new JMenu("Settings");
	private JMenu		menuHelp		=	new JMenu("Help");


	public MenuBar(ActionListener controller){
		super();
		this.controller=controller;
		
		this.addJMenuItem('F', menuFile, "opEn", CMD_OPEN, "Open a new File", KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK);
		this.addJMenuItem('F', menuFile, "saVe", CMD_SAVE, "Saves XSAMS document", KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
		menuFile.add(new JSeparator());
		this.addJMenuItem(menuFile, "exIt", CMD_EXIT, "Quit the Viewer", KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK);
		this.add(menuFile);

		// ---------- EDIT MENU
		this.addJMenuItem('E', menuEdit, "Find", CMD_FIND, "Find text", KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK);
		this.addJMenuItem(menuEdit, "Find Next", CMD_FINDNEXT, "Find next item", KeyEvent.VK_F3, 0);
		this.add(menuEdit);

		// ---------- OPTIONS MENU
		this.addJMenuItem(menuSettings, "Configure", CMD_CONFIG, "Modify configuration", KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK);
		this.add(menuSettings);


		// ---------- HELP MENU
		this.addJMenuItem(menuHelp, "About",CMD_ABOUT, "Program description", -1, 0);
		//menuHelp.add(new JSeparator());
		//this.addJMenuItem(menuHelp, "Usage",CMD_USAGE, "Program usage", KeyEvent.VK_F1, 0);
		this.add(menuHelp);


	}

	/**
	 * Add a mnemonic to the JMenu element
	 * Add a JMenuItem to a JMenu element
	 * Add a tool tip to the JMenuItem
	 * Add an action Listener and an ItemListener 
	 */
	private void addJMenuItem(int mnemonic, JMenu jMenu, String name, String command, String tip, int intKeyEvent, int intInputEvent) {
		jMenu.setMnemonic(mnemonic);
		addJMenuItem(jMenu,name,command,tip,intKeyEvent,intInputEvent);
	}

	/**
	 * Add a JMenuItem to a JMenu element
	 * Add a tool tip to the JMenuItem
	 * Add an action Listener and an ItemListener 
	 */
	private void addJMenuItem(JMenu jMenu, String name, String command, String tip, int intKeyEvent, int intInputEvent) {
		JMenuItem item = new JMenuItem(name);
		jMenu.add(item);
		item.setActionCommand(command);
		item.setToolTipText(tip);
		item.addActionListener(controller);
		if (intKeyEvent != -1) {
			item.setAccelerator(KeyStroke.getKeyStroke(intKeyEvent, intInputEvent));
		}
	}

}
