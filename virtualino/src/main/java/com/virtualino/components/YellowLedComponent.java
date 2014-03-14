package com.virtualino.components;

import com.virtualino.exception.NoMorePinsException;
import java.awt.Color;

/**
 * Virtualino implementation of a yellow LED.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class YellowLedComponent extends LedComponent
{
    public YellowLedComponent(String componentName) throws NoMorePinsException
    {
	super(componentName, Color.yellow);
    }

    @Override
    public String toString()
    {
	return "ino_led_y";
    }
}
