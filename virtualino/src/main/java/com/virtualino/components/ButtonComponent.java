package com.virtualino.components;

import com.virtualino.pins.OutputPin;
import com.virtualino.exception.NoMorePinsException;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

/**
 * Virtualino implementation of a push-button.
 * @author Emanuele 'lemrey' Di Santo
 */
public class ButtonComponent extends GenericComponent
{
    private State state = State.OFF;
    private /*final*/ Image buttonImage;
    private final OutputPin outputPin;
    
    private enum State { OFF, ON };

    public ButtonComponent(String name) throws NoMorePinsException
    {
        setName(name);
        setSize(64, 64);
        setToolTipText(name);

        outputPin = addOutputPin();

        try
        {
            buttonImage = ImageIO.read(getClass().getResourceAsStream(
                "/com/virtualino/ui/button.png")
            );
        } catch (IOException ex)
        {
            Logger.getLogger(ButtonComponent.class.getName()).log(Level.SEVERE, null, ex);
        }

        JLabel labelName = new JLabel(name, JLabel.CENTER);
        add(labelName, BorderLayout.SOUTH);

        doLayout();
    }

    @Override
    public void onLeftClick()
    {
        state = (state == State.OFF ? State.ON : State.OFF);
        outputPin.setValue(state.ordinal());
        repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
	Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(buttonImage, 16, 0, this);
	if (state == State.OFF) {
	    g2.drawString("OFF", 20, 42);
	} else {
	    g2.drawString("ON", 20, 42);
	}
    }
    
    @Override
    public String toString()
    {
	return "ino_button";
    }
}
