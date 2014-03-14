package com.virtualino.net;

import com.virtualino.PinController;
import com.virtualino.pins.Pin;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the communication between Virtualino and the emulator via socket.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class NetCom
{
    private static final int PORT = 5900;
    private static DatagramSocket serverSocket = null;
    private static NetCom netComInstance = null;

    public void forwardPinChange(Pin pin)
    {

	InetAddress hostAddress = null;
	try {
	    hostAddress = InetAddress.getByName("localhost");
	}
	catch (UnknownHostException ex) {
	    Logger.getLogger(NetCom.class.getName()).log(Level.SEVERE, null, ex);
	}

	String sendData = pin.toString();
	DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes(),
		sendData.length(), hostAddress, 5901);

	try {
	    System.out.println("forward pin change : " + sendData);
	    serverSocket.send(sendPacket);
	}
	catch (IOException ex) {
	    Logger.getLogger(NetCom.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    private class UDPThread implements Runnable
    {
	private final DatagramSocket socket;

	public UDPThread(final DatagramSocket socket)
	{
	    this.socket = socket;
	}

	public void run()
	{
	    String data;
	    byte[] receiveData = new byte[32];
	    DatagramPacket rcvPacket = new DatagramPacket(receiveData, receiveData.length);

	    while (true) {
		try {
		    socket.receive(rcvPacket);

		}
		catch (IOException ex) {
		    Logger.getLogger(NetCom.class.getName()).log(Level.SEVERE, null, ex);
		}

		data = new String(rcvPacket.getData(), 0, rcvPacket.getLength());

		System.out.print("receving data: ");

		if (socket.equals(serverSocket)) {
		    processData(data);
		}
	    }
	}
    }

    public static NetCom getInstance()
    {
	if (netComInstance == null) {
	    netComInstance = new NetCom();
	}
	return netComInstance;
    }

    private NetCom()
    {
	try {
	    serverSocket = new DatagramSocket(PORT);
	}
	catch (SocketException e) {
	    System.out.println("DatagramSocket(): " + e.getMessage());
	}

    }

    public void startListening()
    {

	Thread udpThread = new Thread(new UDPThread(serverSocket));

	try {
	    udpThread.start();
	}
	catch (Exception ex) {
	    Logger.getLogger(NetCom.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    private void processData(String data)
    {
	int pin, value;
	PinController pinController = new PinController();

	String[] buf = data.split(",");
	pin = new Integer(buf[0]).intValue();
	value = new Integer(buf[1]).intValue();

	// should never be null
	if (pinController != null) {
	    pinController.pinValueChanged(pin, value);
	}
    }
}