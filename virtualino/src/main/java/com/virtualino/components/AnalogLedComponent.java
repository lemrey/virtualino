package com.virtualino.components;

import com.virtualino.PinListener;
import com.virtualino.exception.NoMorePinsException;
import com.virtualino.pins.InputPin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

/**
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class AnalogLedComponent extends GenericComponent
	implements PinListener
{
    private Image image;
    private BufferedImage bImage;
    private Color componentColor;
    private final InputPin inputPin;

    public AnalogLedComponent(String name, Color color) throws NoMorePinsException
    {
	setName(name);
	setSize(64, 64);
	setToolTipText(name);

	componentColor = color;
	inputPin = addAnalogInputPin(this);

	bImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
	try {
	    if (componentColor == Color.red) {
		image = ImageIO.read(getClass().getResourceAsStream("/com/virtualino/ui/led-red-on.png"));
	    }
	    if (componentColor == Color.green) {
		image = ImageIO.read(getClass().getResourceAsStream("/com/virtualino/ui/led-green-on.png"));
	    }
	    if (componentColor == Color.yellow) {
		image = ImageIO.read(getClass().getResourceAsStream("/com/virtualino/ui/led-yellow-on.png"));
	    }
	}
	catch (IOException ex) {
	    Logger.getLogger(AnalogLedComponent.class.getName()).log(Level.SEVERE, null, ex);
	}

	Graphics2D g = bImage.createGraphics();
	g.drawImage(image, 0, 0, null);

	JLabel labelName = new JLabel(name, JLabel.CENTER);
	add(labelName, BorderLayout.SOUTH);
	doLayout();
    }

    static BufferedImage deepCopy(BufferedImage bi)
    {
	ColorModel cm = bi.getColorModel();
	WritableRaster raster = bi.copyData(null);
	boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    @Override
    public void paintComponent(Graphics g)
    {
	BufferedImage newImg = deepCopy(bImage);

	// darken or brighten the image
	RescaleOp op = new RescaleOp((inputPin.value() / 255.0f) + 0.2f, 0, null);
	op.filter(bImage, newImg);

	((Graphics2D) g).drawImage(newImg, null, 16, 0);
    }

    public void pinChanged(InputPin pin)
    {
	if (pin.equals(inputPin)) {
	    repaint();
	}
    }

    @Override
    public String toString()
    {
	return "ino_analog_led";
    }
}
