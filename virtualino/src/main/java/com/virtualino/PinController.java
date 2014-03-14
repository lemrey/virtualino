package com.virtualino;

import com.virtualino.pins.GenericPin;
import com.virtualino.pins.InputPin;
import com.virtualino.pins.OutputPin;
import com.virtualino.pins.Pin;
import com.virtualino.exception.NoMorePinsException;
import com.virtualino.net.NetCom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * PIN 0:   D0 (rx)  (reserved)
 * PIN 1:   D1 (tx)  (reserved)
 * PIN 2:   D2
 * PIN 3:   D3 (PWM) (timer2B)
 * PIN 4:   D4
 * PIN 5:   D5 (PWM) (timer0B)
 * PIN 6:   D6 (PWM) (timer0A)
 * PIN 7:   D7
 * PIN 8:   B0
 * PIN 9:   B1 (PWM) (timer1A)
 * PIN 10:  B2 (PWM) (timer1B)
 * PIN 11:  B3 (PWM) (timer2A)
 * PIN 12:  B4
 * PIN 13:  B5
 * PIN 14:  C0 (A0 analog)
 * PIN 15:  C1 (A1 analog)
 * PIN 16:  C2 (A2 analog)
 * PIN 17:  C3 (A3 analog)
 * PIN 18:  C4 (A4 analog)
 * PIN 19:  C5 (A5 analog)
 */
/**
 * PinController
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class PinController implements PinWriteCallback
{
    private static ArrayList<String> componentNames;
    private static HashMap<String, List<Integer>> componentPins;
    private static final ArrayList<Pin> inputPinPool;
    private static final ArrayList<Pin> outputPinPool;

    static {

	componentNames = new ArrayList<String>();
	componentPins = new HashMap<String, List<Integer>>();

	inputPinPool = new ArrayList<Pin>();
	outputPinPool = new ArrayList<Pin>();

	// Pins from 2 to 7
	for (int i = 2; i < 8; i++) {
	    Pin p = new Pin(i);
	    inputPinPool.add(p);
	    // PWM pins
	    if ((i == 3) || (i == 5) || (i == 6)) {
		p.setAnalog(true);
	    }
	}

	// Pins from 8 to 19
	for (int i = 8; i < 20; i++) {
	    Pin p = new Pin(i);
	    outputPinPool.add(p);
	    if (i >= 14) { // ADC pins
		p.setAnalog(true);
	    }
	}
    }

    public void clearPins()
    {
	System.out.println("clearing pins!");
	for (Pin p : inputPinPool) {
	    /* be sure to call the method so that it'll trigger
	     * the callback */
	    if (!p.isFree()) {
		//p.setValue(0);
                p.release();
		//p.pinValue = 0;
		//p.getListener().pinChanged(p);
	    }
	}
	for (Pin p : outputPinPool) {
	    // don't really need to
	    if (!p.isFree()) {
                p.release();
		//p.pinValue = 0;
	    }
	}
    }
    
    public void zeroPins() {
        for (Pin p : inputPinPool) {
            p.pinValue = 0;
            if (p.pinListener != null) {
                p.pinListener.pinChanged(p);
            }
        }
    }

    public boolean findComponent(String componentName)
    {
	return componentNames.contains(componentName);
    }

    public void removeComponent(String componentName)
    {
	if (componentNames.contains(componentName)) {
	    componentNames.remove(componentName);
	}

	if (componentPins.containsKey(componentName)) {
	    componentPins.remove(componentName);
	}
    }

    public List<Integer> getComponentPins(String componentName)
    {
	if (componentPins.containsKey(componentName)) {
	    return componentPins.get(componentName);
	}
	else {
	    return new ArrayList<Integer>();
	}
    }

    private Pin getNextFreePin(Iterable<Pin> pool, boolean analog) throws NoMorePinsException
    {
	Pin ret = null;
	for (Pin p : pool) {
	    if (p.isFree()) {
		if (p.isAnalog()) {
		    if (analog) { ret = p; break; }
		    else { continue; }
		}
		else {
		    if (analog) {continue;}
		    else { ret = p; break; }
		}
	    }
	}
	
	if (ret == null) {
	    throw new NoMorePinsException("There are no free pins that can be used for that component");
	}

	return ret;
    }

    private void registerComponentPin(String componentName, GenericPin pin)
    {
	if (!componentNames.contains(componentName)) {
	    componentNames.add(componentName);
	}

	Integer pinNumber = new Integer(pin.number());

	if (!componentPins.containsKey(componentName)) {
	    //System.out.println("mapping " + componentName + " to pin " + pinNumber);

	    ArrayList<Integer> pinList = new ArrayList<Integer>();

	    pinList.add(pinNumber);
	    componentPins.put(componentName, pinList);
	}
	else {
	    System.out.println("adding pin " + pinNumber + " to " + componentName);

	    componentPins.get(componentName).add(pinNumber);
	}
    }

    public InputPin requestInputPin(String componentName, PinListener pinListener, boolean isAnalog) throws NoMorePinsException
    {
	System.out.println("component " + componentName + " requested an input pin");
	if (isAnalog) {
	    System.out.println("analog");
	}

	Pin pin = getNextFreePin(inputPinPool, isAnalog);
	    
	System.out.println("mapping to pin " + pin.number());
	pin.assign();
        pin.setPinListener(pinListener);
        //pin.setPinWriteCallback(this);
        registerComponentPin(componentName, pin);
	
	return pin;
    }

    public OutputPin requestOutputPin(String component, boolean isAnalog) throws NoMorePinsException
    {

	System.out.println("component " + component + " requested an ouput pin");
	if (isAnalog) {
	    System.out.println("analog");
	}

	Pin pin = getNextFreePin(outputPinPool, isAnalog);
            
        System.out.println("mapping to pin " + pin.pinNumber);
        pin.assign();
        pin.setPinWriteCallback(this);
        registerComponentPin(component, pin);	

	return pin;
    }

    // send to components
    public void pinValueChanged(int pin, int value)
    {
	System.out.println("emulator -> pin " + pin + " changed to " + value);

	Pin p = null;
	for (Pin pinp : inputPinPool) {
	    if (pinp.pinNumber == pin) {
		p = pinp;
		break;
	    }
	}

	// should never be null
	if (!p.isFree()) {
	    // don't call setValue or it'll trigger the PinWriteCallback
	    p.pinValue = value;
	    if (p.pinListener != null) {
		p.pinListener.pinChanged(p);
	    }
	}
    }

    // send to emulator
    public void pinWriteCallback(Pin pin)
    {
	NetCom.getInstance().forwardPinChange(pin);
    }
}
