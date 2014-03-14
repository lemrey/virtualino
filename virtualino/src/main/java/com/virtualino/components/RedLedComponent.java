package com.virtualino.components;

import com.virtualino.exception.NoMorePinsException;
import java.awt.Color;

/**
 * Virtualino implementation of Red LED.
 * @author Emanuele 'lemrey' Di Santo
 */
public class RedLedComponent extends LedComponent
{
    public RedLedComponent(String name) throws NoMorePinsException
    {
	super(name, Color.red);
    }

    @Override
    public String toString()
    {
	return "ino_led_r";
    }
}
