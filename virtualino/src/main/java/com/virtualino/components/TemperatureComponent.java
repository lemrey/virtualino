package com.virtualino.components;

import com.virtualino.pins.OutputPin;
import com.virtualino.exception.NoMorePinsException;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class TemperatureComponent extends GenericComponent implements ChangeListener
{
    private JSlider slider;
    private JPanel imgPanel;
    private Image imgTemperature;
    private OutputPin outputPin;

    public TemperatureComponent(String name) throws NoMorePinsException
    {
	setName(name);
	setSize(96, 96);
	setToolTipText(name);

	outputPin = addAnalogOutputPin();

	try {
	    imgTemperature = ImageIO.read(getClass().getResourceAsStream(
		    "/com/virtualino/ui/temperature.png"));
	}
	catch (IOException ex) {
	    Logger.getLogger(TemperatureComponent.class.getName()).log(Level.SEVERE, null, ex);
	}

	slider = new JSlider();
	slider.setMinimum(0);
	slider.setMaximum(100);
	slider.setOrientation(JSlider.VERTICAL);
	slider.addChangeListener(this);

	JLabel label = new JLabel(name, JLabel.CENTER);

	imgPanel = new JPanel()
	{
	    @Override
	    public void paintComponent(Graphics g)
	    {
		g.drawImage(imgTemperature, 16, 16, this);
	    }
	};

	add(imgPanel, BorderLayout.CENTER);
	add(slider, BorderLayout.WEST);
	add(label, BorderLayout.SOUTH);

	doLayout();
    }

    public void stateChanged(ChangeEvent e)
    {
	if (e.getSource().equals(slider)) {
	    outputPin.setValue((1000/100)*slider.getValue());
	}
    }

    @Override
    public String toString()
    {
	return "ino_temperature";
    }
}
