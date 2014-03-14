package com.virtualino.ui;

import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;

/**
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class StatusBar extends JLabel
{
    private Timer timer;
    private static StatusBar instance = null;

    public static StatusBar getInstance()
    {
	if (instance == null) {
	    instance = new StatusBar();
	}
	
	return instance;
    }

    private StatusBar()
    {
	super();
	timer = new Timer();

	super.setText("Ready");
    }

    private class Task extends TimerTask
    {
	private String msg;

	public Task(String msg)
	{
	    this.msg = msg;
	}

	public void run()
	{
	    StatusBar.this.setText(msg);
	}
    }

    public void setStatusMessage(String msg)
    {
	if (timer != null) {
	    timer.schedule(new Task("Ready"), 1500);
	}
	super.setText(msg);
    }
}
