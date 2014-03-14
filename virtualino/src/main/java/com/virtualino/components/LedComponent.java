package com.virtualino.components;

import com.virtualino.PinListener;
import com.virtualino.pins.InputPin;
import com.virtualino.exception.NoMorePinsException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

/**
 * Virtualino implementation of a colored LED. (Red, Green or Yellow)
 * A LedComponent is to be extended to implement one of the three variants
 * @author Emanuele 'lemrey' Di Santo
 */
public abstract class LedComponent extends GenericComponent
        implements PinListener
{
    // Zero is OFF
    private enum State
    {
        OFF, ON
    };
    
    private State componentState;
    private Color componentColor;
    private final InputPin inputPin;
    private /* final */ Image ledImageOn;
    private Image ledImageOff;

    public LedComponent(String name, Color color) throws NoMorePinsException
    {
        setName(name);
        setSize(64, 64);
        setToolTipText(name);

        componentColor = color;
        componentState = State.OFF;

        inputPin = addInputPin(this);

        String imagePath = null;
        if (componentColor.equals(Color.red)) {
            imagePath = "/com/virtualino/ui/led-red-on.png";
        } else if (componentColor.equals(Color.green)) {
            imagePath = "/com/virtualino/ui/led-green-on.png";
        } else if (componentColor.equals(Color.yellow)) {
            imagePath = "/com/virtualino/ui/led-yellow-on.png";
        }

        try {
            ledImageOn = ImageIO.read(getClass().getResourceAsStream(imagePath));
            ledImageOff = ImageIO.read(getClass().getResourceAsStream("/com/virtualino/ui/led-off.png"));
        } catch (IOException ex) {
            Logger.getLogger(LedComponent.class.getName()).log(Level.SEVERE, null, ex);
        }

        JLabel labelName = new JLabel(name, JLabel.CENTER);
        add(labelName, BorderLayout.SOUTH);

        doLayout();
    }

    private void setState(State state)
    {
        componentState = state;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        if (componentState == State.ON) {
            g.drawImage(ledImageOn, 16, 0, null);
        } else {
            g.drawImage(ledImageOff, 16, 0, null);
        }
    }

    public void pinChanged(InputPin pin)
    {
        if (pin.equals(inputPin)) {
            if (pin.value() == 0) {
                setState(State.OFF);
            } else {
                setState(State.ON);
            }
        }
    }
}