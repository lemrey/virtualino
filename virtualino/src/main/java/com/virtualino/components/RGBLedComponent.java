package com.virtualino.components;

import com.virtualino.PinListener;
import com.virtualino.pins.InputPin;
import com.virtualino.exception.NoMorePinsException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

/**
 * An RGB LED (3 pins) implementation.
 * This component requires three PWM pins.
 * @author Emanuele 'lemrey' Di Santo
 */
public class RGBLedComponent extends GenericComponent
	implements PinListener
{
    private Image imgLed;
    private /*final*/ InputPin pinRed;
    private /*final*/ InputPin pinGreen;
    private /*final*/ InputPin pinBlue;
    Ellipse2D circle = new Ellipse2D.Float(4, 4, 12, 12);

    public RGBLedComponent(String name) throws NoMorePinsException
    {
	setName(name);
	setSize(64, 64);
	setToolTipText(name);

	try {
	    pinRed = addAnalogInputPin(this);
	    pinGreen = addAnalogInputPin(this);
	    pinBlue = addAnalogInputPin(this);
	}
	catch (NoMorePinsException ex) {
	    /* we must make sure to release all those
	     * that were actually registered.
	     */
	    if (pinRed != null) {
		pinRed.release();
	    }
	    if (pinGreen != null) {
		pinGreen.release();
	    }
	    if (pinBlue != null) {
		pinBlue.release();
	    }
	    throw ex;
	}

	try {
	    imgLed = ImageIO.read(getClass().getResourceAsStream("/com/virtualino/ui/led-rgb.png"));
	}
	catch (IOException ex) {
	    Logger.getLogger(AnalogLedComponent.class.getName()).log(Level.SEVERE, null, ex);
	}

	JLabel labelName = new JLabel(name, JLabel.CENTER);
	add(labelName, BorderLayout.SOUTH);

	doLayout();
    }

    @Override
    public void paintComponent(Graphics g)
    {
	Graphics2D g2 = (Graphics2D) g;
	Color c = new Color(pinRed.value(), pinGreen.value(), pinBlue.value());
	g2.drawImage(imgLed, 16, 0, null);
	g2.setColor(c);
	g2.fill(circle);
    }

    public void pinChanged(InputPin pin)
    {
	repaint();
    }

    @Override
    public String toString()
    {
	return "ino_rgb_led";
    }
}
