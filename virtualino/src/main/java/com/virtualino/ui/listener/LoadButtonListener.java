package com.virtualino.ui.listener;

import com.ardublock.core.Context;
import com.virtualino.components.*;
import com.virtualino.ui.VirtualinoFrame;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author lemrey
 */
public class LoadButtonListener implements ActionListener
{
    private Context context;
    private JFileChooser fileChooser;
    private VirtualinoFrame virtualinoFrame;

    public LoadButtonListener(VirtualinoFrame frame)
    {
        virtualinoFrame = frame;
	context = Context.getContext();
	
	fileChooser = new JFileChooser();
	FileFilter filter = new FileNameExtensionFilter("Block files", "blocks");
	fileChooser.setFileFilter(filter);
    }

    public void actionPerformed(ActionEvent ae)
    {
	try {
                        
            // clear the workspace
            virtualinoFrame.resetWorkspace();

	    int result = fileChooser.showOpenDialog(virtualinoFrame);
	    if (result == JFileChooser.APPROVE_OPTION) {

		File file = fileChooser.getSelectedFile();
		if (!file.exists()) {
		    System.out.println("mm ??");
		    return;
		}
		
		context.loadArduBlockFile(file);

		String path = file.getPath().replace(".blocks", ".com");

		BufferedReader in = new BufferedReader(new FileReader(path));
		int items = new Integer(in.readLine()).intValue();
		System.out.println("found : " + items);

		while (items-- > 0) {

		    GenericComponent e = null;
		    String genera = in.readLine();
		    String name = in.readLine();

		    try {
			if (genera.equals("ino_led_r")) {
			    e = new RedLedComponent(name);
			}
			else if (genera.equals("ino_led_g")) {
			    e = new GreenLedComponent(name);
			}
			else if (genera.equals("ino_led_y")) {
			    e = new YellowLedComponent(name);
			}
			else if (genera.equals("ino_button")) {
			    e = new ButtonComponent(name);
			}
			else if (genera.equals("ino_slider")) {
			    e = new SliderComponent(name);
			}
			else if (genera.equals("ino_ldr")) {
			    e = new LDRComponent(name);
			}
			else if (genera.equals("ino_temperature")) {
			    e = new TemperatureComponent(name);
			}
			else if (genera.equals("ino_analog_led")) {
			    /* we only load red analog leds.. :\
			     * a the AnalogLedComponent should be subclassed
			     * just like LedComponent
			     */
			    e = new AnalogLedComponent(name, Color.red);
			}

			int x = new Integer(in.readLine()).intValue();
			int y = new Integer(in.readLine()).intValue();

			e.setLocation(x, y);
			virtualinoFrame.addVirtualinoComponent(e);
		    }
		    catch (Exception ex) {
			System.out.println(ex.getMessage());
		    }
		}
		in.close();
	    }
	}
	catch (IOException ex) {
	    JOptionPane.showOptionDialog(virtualinoFrame,
		    ex.getMessage(),
		    "Error",
		    JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE,
		    null, null, JOptionPane.OK_OPTION);
	}
    }
}
