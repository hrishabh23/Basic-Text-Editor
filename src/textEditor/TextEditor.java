package textEditor;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.*;

public class TextEditor extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private JTextArea area = new JTextArea(20, 120);
	private JTextArea checker = new JTextArea();
	private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
	private String currentFile = "Untitled";
	private boolean changed = false;
	private boolean isBold = false;
	private boolean isItalic = false;
	
	public TextEditor(){
		/*
		 * Setting the font to be used in the TextArea
		 * Adding the above textArea in the JScrollPane
		 * Adding the JScrollPane to the JFrame
		 */
		area.setFont(new Font("Monospaced", Font.PLAIN, 12));
		JScrollPane scroll = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scroll, BorderLayout.CENTER);
		
		/*
		 * JMenuBar instance is defined
		 * Instance is linked to the JFrame
		 * Menus named "File" and "Edit" are defined and added to JMenuBar
		 * 
		 */
		JMenuBar JMB = new JMenuBar();
		setJMenuBar(JMB);
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMB.add(fileMenu);
		JMB.add(editMenu);
		
		/*
		 * Options are added in the "File" Menu
		 * Separator is used to separate two unrelated menu options inside the same menu by adding a horizontal rule
		 * Icons of menu options are set to null. Otherwise shows icons for each menu option
		 */
		fileMenu.add(New);
		fileMenu.add(Open);
		fileMenu.addSeparator();
		fileMenu.add(Save);
		fileMenu.add(SaveAs);
		fileMenu.addSeparator();
		fileMenu.add(Quit);
		
		for(int i=0; i<7; i++){
			if(fileMenu.getItem(i)!=null){			//returns null when the return value is not a menu item (in this case when separator)
				fileMenu.getItem(i).setIcon(null);
			}
		}
		
		/*
		 * Options are added in the "Edit" Menu
		 */
		
		editMenu.add(Cut);
		editMenu.add(Copy);
		editMenu.add(Paste);
		editMenu.addSeparator();
		editMenu.add(Bold);
		editMenu.add(Italics);
		
		/*
		 * Names to be displayed are set
		 * default names are cut-to-clipboard, copy-to-clipboard etc.
		 * Names are only set for these Actions because these are not objects of AbstractAction
		 */
		
		editMenu.getItem(0).setText("Cut");
		editMenu.getItem(1).setText("Copy");
		editMenu.getItem(2).setText("Paste");
		
		/*
		 * Toolbar is declared and buttons are added
		 */
		
		
		JToolBar tool = new JToolBar();
		add(tool, BorderLayout.NORTH);
		tool.add(New);
		tool.add(Open);
		tool.add(Save);
		tool.addSeparator();
		
		JButton cut = tool.add(Cut),
		cop = tool.add(Copy), 
		pas = tool.add(Paste);
		tool.addSeparator();
		JButton bol = tool.add(Bold);
		JButton ita = tool.add(Italics);
		
		
		cut.setText(null); cut.setIcon(new ImageIcon("images/cut.gif"));	//Icons of buttons are set
		cop.setText(null); cop.setIcon(new ImageIcon("images/copy.gif"));
		pas.setText(null); pas.setIcon(new ImageIcon("images/paste.gif"));
		bol.setFont(new Font("Monospaced", Font.BOLD, 18));
		bol.setText(" B ");
		ita.setFont(new Font("Monospaced", Font.ITALIC, 18));
		ita.setText(" I ");
		
		Save.setEnabled(false);		//Save and  Save As options are disabled until any change is made
		SaveAs.setEnabled(false);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		area.addKeyListener(notifyChange);
		setTitle(currentFile);
		setVisible(true);
	}
	
	private KeyListener notifyChange = new KeyAdapter(){
		public void keyPressed(KeyEvent e){
			
				changed = true;
				Save.setEnabled(true);
				SaveAs.setEnabled(true);
			
		}
	};
	
	
	//Defining Actions start here
	
	
	/*
	 * Prompts to save the file which is to be closed
	 * Lets to choose a file from JFileChooser
	 */
	Action Open = new AbstractAction("Open", new ImageIcon("images/open.gif")) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e){
			saveOld();
			if(dialog.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				
				readInFile(dialog.getSelectedFile().getAbsolutePath());
			}
			SaveAs.setEnabled(true);
			
		}
	};
	
	
	/*
	 * If file has already been saved previously then saves automatically
	 * If not then executes saveAs
	 */
	Action Save = new AbstractAction("Save", new ImageIcon("images/save.gif")) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e){
			if(!currentFile.equals("Untitled"))
				saveFile(currentFile);
			else
				saveFileAs();
		}
	};
	
	/*
	 * Prompts to save the currently opened file
	 * Clears all the data related to previous file
	 */
	Action New = new AbstractAction("New", new ImageIcon("images/new.gif")) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e){
			saveOld();
			currentFile="Untitled";
			changed = false;
			area.setText(null);
			setTitle(currentFile);
		}
	};

	/*
	*Executes saveFileAs method
	*/
	
	Action SaveAs = new AbstractAction("Save as...") {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			saveFileAs();
		}
	};

	/*
	*Prompts to save the opened file
	*Exits
	*/
	
	Action Quit = new AbstractAction("Quit") {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e){
			saveOld();
			System.exit(0);
		}
	};

	/*
	*Makes the text bold and not bold when runs
	*/
	
	Action Bold = new AbstractAction("Bold"){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e){
			isBold=!isBold;
			area.setFont(new Font("Monospaced", getFontStyle(), 12));
			
		}
	};

	/*
	*Makes the text Italic and not italic when runs
	*/
	
	Action Italics = new AbstractAction("Italics"){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e){
			isItalic=!isItalic;
			area.setFont(new Font("Monospaced", getFontStyle(), 12));
			
		}
	};
	
	/*
	*Defining the actions Cut, Copy and Paste
	*
	*/
	ActionMap m = area.getActionMap();
	Action Cut = m.get(DefaultEditorKit.cutAction);
	Action Copy = m.get(DefaultEditorKit.copyAction);
	Action Paste = m.get(DefaultEditorKit.pasteAction);

	/*
	*Lets to choose name and save
	*/

	private void saveFileAs(){
		if(dialog.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
			saveFile(dialog.getSelectedFile().getAbsolutePath());
		}
	}


	/*
	*Prompts to save the file
	*/
	private void saveOld(){
		if(changed){
			if(JOptionPane.showConfirmDialog(this, "Would you like to save "+ currentFile+" ?", "Save", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
				saveFile(currentFile);
		}
	}
	
	private void readInFile(String fileName){
		try {
			FileReader r = new FileReader(fileName);
			area.read(r, null);
			r.close();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
		}
		catch(IOException e){
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(this, "Editor can't find the file called "+fileName);
		}
	}
	
	private void saveFile(String fileName){
		try{
			FileWriter w = new FileWriter(fileName);
			area.write(w);
			w.close();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
			Save.setEnabled(false);
		}
		catch(IOException e){}
	}
	
	private int getFontStyle(){
		
		int weight=(isBold)?((isItalic)?(Font.BOLD+Font.ITALIC):(Font.BOLD)):((isItalic)?(Font.ITALIC):(Font.PLAIN));
		return weight;
	}
	
	public static void main(String[] args) {
		new TextEditor();
	}

}
