package org.vamdc.validator.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

public class TextPopup extends JPopupMenu{

	private static final long serialVersionUID = 1136633553671538869L;

    Clipboard clipboard;

    UndoManager undoManager;

    JMenuItem jmenuItem_undo;
    JMenuItem jmenuItem_cut;
    JMenuItem jmenuItem_copy;
    JMenuItem jmenuItem_paste;
    JMenuItem jmenuItem_delete;
    JMenuItem jmenuItem_selectAll;

    JTextComponent jtextComponent;

    public TextPopup()
    {
        undoManager = new UndoManager();

        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        jmenuItem_undo = new JMenuItem("undo");
        jmenuItem_undo.setEnabled(false);
        jmenuItem_undo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                undoManager.undo();
            }
        });

        this.add(jmenuItem_undo);

        this.add(new JSeparator());

        jmenuItem_cut = new JMenuItem("cut");
        jmenuItem_cut.setEnabled(false);
        jmenuItem_cut.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                jtextComponent.cut();
            }
        });

        this.add(jmenuItem_cut);

        jmenuItem_copy = new JMenuItem("copy");
        jmenuItem_copy.setEnabled(false);
        jmenuItem_copy.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                jtextComponent.copy();
            }
        });

        this.add(jmenuItem_copy);

        jmenuItem_paste = new JMenuItem("paste");
        jmenuItem_paste.setEnabled(false);
        jmenuItem_paste.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                jtextComponent.paste();
            }
        });

        this.add(jmenuItem_paste);

        jmenuItem_delete = new JMenuItem("delete");
        jmenuItem_delete.setEnabled(false);
        jmenuItem_delete.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                jtextComponent.replaceSelection("");
            }
        });

        this.add(jmenuItem_delete);

        this.add(new JSeparator());

        jmenuItem_selectAll = new JMenuItem("select all");
        jmenuItem_selectAll.setEnabled(false);
        jmenuItem_selectAll.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                jtextComponent.selectAll();
            }
        });

        this.add(jmenuItem_selectAll);
    }

    public void add(JTextComponent jtextComponent)
    {
        jtextComponent.addMouseListener(new MouseAdapter()
        {
            public void mouseReleased(MouseEvent event)
            {
                if (event.getButton() == 3)
                {
                    processClick(event);
                }
            }
        });

        jtextComponent.getDocument().addUndoableEditListener(new UndoableEditListener()
        {
            public void undoableEditHappened(UndoableEditEvent event)
            {
                undoManager.addEdit(event.getEdit());
            }
        });
    }

    private void processClick(MouseEvent event)
    {
        jtextComponent = (JTextComponent)event.getSource();

        boolean enableUndo = jtextComponent.isEditable() && undoManager.canUndo();
        boolean enableCut = false;
        boolean enableCopy = false;
        boolean enablePaste = false;
        boolean enableDelete = false;
        boolean enableSelectAll = false;

        String selectedText = jtextComponent.getSelectedText();
        String text = jtextComponent.getText();

        if (text != null)
        {
            if (text.length() > 0)
            {
                enableSelectAll = true;
            }
        }

        if (selectedText != null)
        {
            if (selectedText.length() > 0)
            {
                enableCopy = true;
                if (jtextComponent.isEditable()){
                	enableCut = true;
                	enableDelete = true;
                }
            }
        }

        try
        {
            if (jtextComponent.isEditable()&&clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor))
            {
                enablePaste = true;
            }
        }
        catch (Exception exception)
        {
            System.out.println("exception"+exception);
        }

        jmenuItem_undo.setEnabled(enableUndo);
        jmenuItem_cut.setEnabled(enableCut);
        jmenuItem_copy.setEnabled(enableCopy);
        jmenuItem_paste.setEnabled(enablePaste);
        jmenuItem_delete.setEnabled(enableDelete);
        jmenuItem_selectAll.setEnabled(enableSelectAll);

        this.show(jtextComponent,event.getX(),event.getY());
    }
}
