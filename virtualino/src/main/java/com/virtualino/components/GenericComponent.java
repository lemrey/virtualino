package com.virtualino.components;

import com.virtualino.PinController;
import com.virtualino.PinListener;
import com.virtualino.exception.NoMorePinsException;
import com.virtualino.pins.GenericPin;
import com.virtualino.pins.InputPin;
import com.virtualino.pins.OutputPin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * A generic Virtualino component with a notion of input and output pins.
 * Implements basic mouse handling functionality. Keep in mind that what is an
 * Input for a component is an Output for the emulator and vice-versa.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public abstract class GenericComponent extends JComponent
{
    private Point dragPoint = new Point();
    private MouseEventsHandler mouseEventsHandler = new MouseEventsHandler();
    private ArrayList<GenericPin> registeredPins = new ArrayList<GenericPin>();

    private class MouseEventsHandler extends MouseAdapter
    {
	@Override
	public void mouseClicked(MouseEvent evt)
	{
	    mouseClickedHandler(evt);
	}

	@Override
	public void mousePressed(MouseEvent evt)
	{
	    mousePressedHandler(evt);
	}

	@Override
	public void mouseReleased(MouseEvent evt)
	{
	    mouseReleasedHandler(evt);
	}

	@Override
	public void mouseDragged(MouseEvent evt)
	{
	    mouseDraggedHandler(evt);
	}
    }

    /* 
     * Some components might not paint themselves using this method 
     * like the slider or temperature sensor
     * @Override
     * protected abstract void paintComponent(Graphics g);
     */

    /* Ensure that this is called by subclasses */
    public GenericComponent()
    {
	addMouseListener(mouseEventsHandler);
	addMouseMotionListener(mouseEventsHandler);

	setLayout(new BorderLayout());
	setBorder(BorderFactory.createLineBorder(Color.black));
    }

    private void mousePressedHandler(MouseEvent evt)
    {
	setCursor(new Cursor(Cursor.HAND_CURSOR));

	dragPoint.x = evt.getX();
	dragPoint.y = evt.getY();
    }

    private void mouseDraggedHandler(MouseEvent evt)
    {
	int x, y;
	evt = SwingUtilities.convertMouseEvent(this, evt, getParent());

	x = evt.getX() - dragPoint.x;
	y = evt.getY() - dragPoint.y;

	x = Math.max(x, 0);
	x = Math.min(x, getParent().getWidth() - WIDTH);

	y = Math.max(y, 0);
	y = Math.min(y, getParent().getHeight() - HEIGHT);

	setLocation(x, y);
    }

    private void mouseReleasedHandler(MouseEvent evt)
    {
	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private void mouseClickedHandler(MouseEvent evt)
    {
	if (SwingUtilities.isRightMouseButton(evt)) {
	    JPopupMenu menu = createPopupMenu();
	    menu.show(this, evt.getX(), evt.getY());
	}
	else {
	    //System.out.println("clicked " + getName());
	    onLeftClick();
	}
    }

    protected void onLeftClick() {;}
    //protected void onRightClick() {;}

    protected JPopupMenu createPopupMenu()
    {
	JPopupMenu menu = new JPopupMenu();
	JMenuItem itemTitle = new JMenuItem("options");
	JMenuItem itemRename = new JMenuItem("rename");
	JMenuItem itemRemove = new JMenuItem("remove");

	menu.add(itemTitle);
	menu.addSeparator();
	menu.add(itemRename);
	menu.add(itemRemove);

	itemTitle.setEnabled(false);
	itemRename.setEnabled(false);

	itemRemove.addActionListener(new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
		JMenuItem item = (JMenuItem) e.getSource();
		JComponent parent = (JComponent) getParent();

		if (item.getText().equals("remove")) {

		    PinController pinController = new PinController();
		    pinController.removeComponent(GenericComponent.this.getName());
		    
		    // release all registered pins when removing the component
		    for (GenericPin i : registeredPins) {
			i.release();
		    }
		    
		    parent.remove(GenericComponent.this);
		    parent.repaint();
		}
	    }
	});

	return menu;
    }

    /* Adds an input pin to the component */
    public InputPin addInputPin(PinListener listener) throws NoMorePinsException
    {
	InputPin pin;
	PinController pinController = new PinController();

	pin = pinController.requestInputPin(getName(), listener, false);
	registeredPins.add(pin);
	return pin;
    }

    /* Adds an output pin to the component */
    public OutputPin addOutputPin() throws NoMorePinsException
    {
	OutputPin pin;
	PinController pinController = new PinController();

	pin = pinController.requestOutputPin(getName(), false);
	registeredPins.add(pin);
	return pin;
    }

    /* Adds an analog output pin to the component */
    public OutputPin addAnalogOutputPin() throws NoMorePinsException
    {
	OutputPin pin;
	PinController pinController = new PinController();

	pin = pinController.requestOutputPin(getName(), true);
	registeredPins.add(pin);
	return pin;
    }

    /* Adds an analog input pin to the component */
    public InputPin addAnalogInputPin(PinListener listener) throws NoMorePinsException
    {
	InputPin pin;
	PinController pinController = new PinController();

	pin = pinController.requestInputPin(getName(), listener, true);
	registeredPins.add(pin);
	return pin;
    }
    
    
    /* Components ovveride this method in order to provide their
     * respective block genus name. It provides some utility
     * to create the corresponding Block instance in VirtualinoFrame
     */
    @Override
    public abstract String toString();
}
