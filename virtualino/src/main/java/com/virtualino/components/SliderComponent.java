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
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class SliderComponent extends GenericComponent implements ChangeListener
{
    private OutputPin outputPin;
    private final JSlider slider;
    private final JPanel imgPanel;
    private Image sliderImage;

    public SliderComponent(String name) throws NoMorePinsException
    {
	setName(name);
	setSize(64, 64);

	outputPin = addAnalogOutputPin();

	try {
	    sliderImage = ImageIO.read(getClass().getResourceAsStream(
		    "/com/virtualino/ui/slider.png"));
	}
	catch (IOException ex) {
	    Logger.getLogger(SliderComponent.class.getName()).log(Level.SEVERE, null, ex);
	}

	slider = new JSlider();
	slider.setMinimum(0);
	slider.setMaximum(100);
	slider.setOrientation(JSlider.HORIZONTAL);
	slider.addChangeListener(this);

	JLabel label = new JLabel(name);
	label.setHorizontalAlignment(SwingConstants.CENTER);

	imgPanel = new JPanel()
	{
	    @Override
	    public void paintComponent(Graphics g)
	    {
		g.drawImage(sliderImage, 16, 0, this);
	    }
	};

	add(imgPanel, BorderLayout.CENTER);
	add(slider, BorderLayout.SOUTH);
	add(label, BorderLayout.NORTH);

	doLayout();
    }

    public void stateChanged(ChangeEvent e)
    {
	if (e.getSource().equals(slider)) {
	    outputPin.setValue((5000/100)*slider.getValue());
	}
    }

    @Override
    public String toString()
    {
	return "ino_slider";
    }
}
