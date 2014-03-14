package com.virtualino.components;

import com.virtualino.PinListener;
import com.virtualino.exception.NoMorePinsException;
import com.virtualino.pins.InputPin;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

/**
 * Virtualino implementation of a buzzer.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class BuzzerComponent extends GenericComponent implements PinListener
{
    private /*final*/ Clip myClip;
    private /*final*/ Image buzzerImage;
    private final InputPin inputPin;

    public BuzzerComponent(String name) throws NoMorePinsException
    {
	setName(name);
	setSize(64, 64);
	setToolTipText(name);

	inputPin = addInputPin(this);

	//File file = new File("/home/lemrey/spkr_a.wav");

	try {
	    AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(
		    getClass().getResourceAsStream("/com/virtualino/sound/spkr_a.wav")));
	    DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
	    myClip = (Clip) AudioSystem.getLine(info);
	    myClip.open(ais);

	    buzzerImage = ImageIO.read(getClass().getResourceAsStream(
		    "/com/virtualino/ui/speaker.png"));
	}
	catch (Exception ex) {
	    Logger.getLogger(BuzzerComponent.class.getName()).log(Level.SEVERE, null, ex);
	}

	JLabel labelName = new JLabel(name, JLabel.CENTER);
	add(labelName, BorderLayout.SOUTH);

	doLayout();
    }

    @Override
    protected JPopupMenu createPopupMenu()
    {
	JPopupMenu menu = super.createPopupMenu();
	return menu;
    }

    @Override
    public void onLeftClick()
    {
	myClip.setFramePosition(0);
	myClip.loop(0);
    }

    @Override
    public void paintComponent(Graphics g)
    {
	g.drawImage(buzzerImage, 16, 0, this);
    }

    public synchronized void pinChanged(InputPin pin)
    {
	if (pin.equals(inputPin)) {
	    if (pin.value() == 1) {
		myClip.setFramePosition(0);
		myClip.loop(Clip.LOOP_CONTINUOUSLY);
	    }
	    else {
		myClip.stop();
	    }
	}
    }

    @Override
    public String toString()
    {
	return "ino_buzzer";
    }
}
