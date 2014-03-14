package com.virtualino.pins;

import com.virtualino.PinListener;
import com.virtualino.PinWriteCallback;

/**
 *
 * @author lemrey
 */
public class Pin implements InputPin, OutputPin, GenericPin
{
    public int pinValue = 0;
    private String pinName;
    public final int pinNumber;
    private boolean isFree = true;
    private boolean isAnalog = false;
    public PinListener pinListener;
    private PinWriteCallback pinWriteCallback;

    public String getPinName() {
	return pinName;
    }
    
    public Pin(int pinNumber)
    {
	this.pinNumber = pinNumber;
	this.pinName = new String();
    }

    public void setPinName(String name)
    {
	this.pinName = name;
    }

    public Pin(int pinNumber, String pinName)
    {
	this.pinNumber = pinNumber;
	this.pinName = pinName;
    }

    public void setAnalog(boolean isAnalog)
    {
	this.isAnalog = isAnalog;
    }

    public boolean isAnalog()
    {
	return isAnalog;
    }

    public boolean isFree()
    {
	return isFree;
    }

    public void assign()
    {
	isFree = false;
    }

    public void release()
    {
	isFree = true;
	pinListener = null;
	pinWriteCallback = null;

	pinValue = 0;
    }

    public int number()
    {
	return pinNumber;
    }

    public int value()
    {
	return pinValue;
    }

    public void setValue(int value)
    {
	pinValue = value;

	if (pinWriteCallback != null) {
	    pinWriteCallback.pinWriteCallback(this);
	}
	else {
	    System.out.println("setValue on a null callback pin");
	}
    }

    public void setPinWriteCallback(PinWriteCallback callback)
    {
	pinWriteCallback = callback;
    }

    public void setPinListener(PinListener listener)
    {
	pinListener = listener;
    }

    public PinListener getListener()
    {
	return pinListener;
    }

    @Override
    public String toString()
    {
	/*
	 * Actually produces wrong output if used on
	 * analog output pins (3,5,6), however we don't send messages
	 * related to these pin, we only receive them.
	 * Works good for analog input pins
	 */
	String ret = new String();
	if (isAnalog && pinNumber >= 14) {
	    ret = "A" + (pinNumber - 14) + "," + pinValue;
	}
	else {
	    ret += pinNumber + "," + pinValue;
	}
	return ret;
    }
}
