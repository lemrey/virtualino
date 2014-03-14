package com.virtualino.ui;

import com.virtualino.PinController;
import com.virtualino.components.AnalogLedComponent;
import com.virtualino.components.ButtonComponent;
import com.virtualino.components.BuzzerComponent;
import com.virtualino.components.LDRComponent;
import com.virtualino.components.LedComponent;
import com.virtualino.components.RGBLedComponent;
import com.virtualino.components.SliderComponent;
import com.virtualino.components.TemperatureComponent;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class EmulatorFrame extends JComponent
{
    public EmulatorFrame()
    {
	setVisible(true);
	setSize(580, 500);
    }

    public String printInstructions()
    {
	List<Integer> pinList;
	PinController pinC = new PinController();
	StringBuilder ret = new StringBuilder();
	
	for (Component c : getComponents()) {
	    
	    String name = c.getName();
	    pinList = pinC.getComponentPins(name);

	    if (c instanceof LedComponent || c instanceof AnalogLedComponent) {
		ret.append("Connect ");
		ret.append(name);
		ret.append("'s longer lead to Arduino pin ");
		ret.append(pinList.get(0));
		ret.append("\nConnect ");
		ret.append(name);
		ret.append("'s shorter lead to Ground");
		ret.append(" using a 1 kilo Ohm resistor\n");
		ret.append("This value is not critical and maybe adjusted slightly: ");
		ret.append("the lower it is, the brighter the LED will be\n");
	    }
	    else if (c instanceof ButtonComponent) {
		ret.append("A button has four leads, two on each side\n");
		ret.append("Connect any of ");
		ret.append(name);
		ret.append("'s leads to Arduino pin ");
		ret.append(pinList.get(0));
		ret.append("\nConnect ");
		ret.append(name);
		ret.append("'s lead on the other side to ");
		ret.append("Ground using a 10 kilo Ohm resistor");
		ret.append("\nConnect ");
		ret.append(name);
		ret.append("'s other lead on that same side to Vcc\n");
	    }
	    else if (c instanceof SliderComponent) {
		ret.append("Sliders have three lead, so\n");
		ret.append("Connect ");
		ret.append(name);
		ret.append("'s middle lead to Arduino pin ");
		ret.append("A"+(pinList.get(0)-14));
		ret.append("\nConnect ");
		ret.append(name);
		ret.append("'s remaining two leads to Ground and Vcc\n");
	    }
	    else if (c instanceof LDRComponent) {
		ret.append("Connect one of ");
		ret.append(name);
		ret.append("'s leads to Arduino pin ");
		ret.append("A"+(pinList.get(0)-14));
		ret.append("\nConnect that same lead to Ground using a ");
		ret.append(" 10 kilo Ohm resistor");
		ret.append("\nConnect ");
		ret.append(name);
		ret.append("'s other lead to Vcc\n");
	    }
	    else if (c instanceof TemperatureComponent) {
		ret.append("The temperature sensor has two sides: ");
		ret.append("one side is curved and the other is flat.");
		ret.append("Look at the flat side and,\n");
		ret.append("Connect ");
		ret.append(name);
		ret.append("'s middle lead to Arduino pin ");
		ret.append("A"+(pinList.get(0)-14));
		ret.append("\nConnect ");
		ret.append(name);
		ret.append("'s left lead to Ground");
		ret.append("\nConnect ");
		ret.append(name);
		ret.append("'s other lead to Vcc\n");
	    }
	    else if (c instanceof BuzzerComponent) {
		ret.append("Buzzers don't have a polarity, so\n");
		ret.append("Connect one of ");
		ret.append(name);
		ret.append("'s leads to Arduino pin ");
		ret.append(pinList.get(0));
		ret.append("\nConnect ");
		ret.append(name);
		ret.append("'s other lead to Ground");
		ret.append(" using a 100 Ohm ~ resistor\n");
		ret.append("\n");
	    }
	    else if (c instanceof RGBLedComponent) {
		ret.append("Connect ");
		ret.append(name);
		ret.append("'s longer lead to Ground\n");
		ret.append("Connect the one lead to its left to Arduino pin ");
		ret.append(pinList.get(0));
		ret.append("\nConnect the two other leads to its right to Arduino pins ");
		ret.append(pinList.get(1));
		ret.append(" and ");
		ret.append(pinList.get(2));
		ret.append(" respectively\n");
	    }
	}

	return ret.toString();
    }
}
