package com.virtualino.ui.listener;

import com.ardublock.core.Context;
import com.virtualino.ui.EmulatorFrame;
import edu.mit.blocks.controller.WorkspaceController;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class SaveButtonListener implements ActionListener
{
    private EmulatorFrame emulatorFrame;
    private JFileChooser fileChooser;
    private Context context;

    public SaveButtonListener(EmulatorFrame frame)
    {
	emulatorFrame = frame;
	context = Context.getContext();
	fileChooser = new JFileChooser();
	
	FileFilter filter = new FileNameExtensionFilter("Block files", "blocks");
	fileChooser.setFileFilter(filter);
    }

    public void actionPerformed(ActionEvent ae)
    {
	System.out.println("SAVE BUTTON");
	try {
	    WorkspaceController workspaceController = context.getWorkspaceController();
	    String saveString = workspaceController.getSaveString();
	    File file = null;
	    int chooseResult;
	    chooseResult = fileChooser.showSaveDialog(((JComponent)ae.getSource()).getParent());
	    if (chooseResult == JFileChooser.APPROVE_OPTION) {
		file = fileChooser.getSelectedFile();
		//file = checkFileSuffix(file);
		if (!file.getAbsolutePath().endsWith(".blocks")) {
		    file  = new File(file.getAbsolutePath()+".blocks");
		}
		if (file != null) {
		    if (file.exists()) {
			int optionValue = JOptionPane.showOptionDialog(null,
				"Would you like to overwrite existing files?",
				"Confirm",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, null, JOptionPane.YES_OPTION);

			if (optionValue != JOptionPane.YES_OPTION) {
			    return;
			}
		    }
		    context.saveArduBlockFile(file, saveString);
		    //System.out.println(saveString);
		    //saveFilePath = saveFile.getAbsolutePath();
		    //saveFileName = saveFile.getName();
		    //context.setWorkspaceChanged(false);
		}
	    }


	    FileWriter output = null;
	    File filec = new File(file.getAbsolutePath().replace(".blocks", ".com"));

	    output = new FileWriter(filec);
	    BufferedWriter writer = new BufferedWriter(output);
	    writer.write(String.valueOf(emulatorFrame.getComponents().length));
	    writer.newLine();

	    for (Component c : emulatorFrame.getComponents()) {

		// this will write their Block genus
		writer.write(c.toString());
		writer.newLine();
		writer.write(c.getName());
		writer.newLine();
		writer.write(String.valueOf(c.getX()));
		writer.newLine();
		writer.write(String.valueOf(c.getY()));
		writer.newLine();
	    }
	    writer.close();
	    output.close();

	}
	catch (Exception e) {
	    //throw new RuntimeException(e);
	}
	finally {
	    /*if (output != null) {
		try {
		    output.close();
		}
		catch (IOException e) {
		    // Ignore issues during closing
		}
	    }*/
	}
    }
}
