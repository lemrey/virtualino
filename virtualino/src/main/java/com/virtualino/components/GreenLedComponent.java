package com.virtualino.components;

import com.virtualino.exception.NoMorePinsException;
import java.awt.Color;

/**
 * Virtualino implementation of a Green LED.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class GreenLedComponent extends LedComponent
{
    public GreenLedComponent(String componentName) throws NoMorePinsException
    {
	super(componentName, Color.green);
    }

    @Override
    public String toString()
    {
	return "ino_led_g";
    }
}
