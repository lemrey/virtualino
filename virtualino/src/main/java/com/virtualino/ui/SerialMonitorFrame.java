package com.virtualino.ui;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultCaret;

/**
 * A serial monitor for both hardware and software (pty) interfaces.
 *
 * @author Emanue 'lemrey' Di Santo
 */
public class SerialMonitorFrame extends javax.swing.JFrame
{
    private CommPort commPort;
    private SerialPort serialPort;
    private DefaultCaret defCaret; //for autoscroll
    private Thread ptyWorker;
    private final int SOFT_INTERFACE = 0;
    private final int HARD_INTERFACE = 1;

    public SerialMonitorFrame()
    {
	initComponents();
	// center on screen
	setLocationRelativeTo(null);

	defCaret = (DefaultCaret) jTextAreaSerial.getCaret();
    }

    private void connect(String portName, int baudRate) throws Exception
    {
	CommPortIdentifier portIdentifier;
	portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

	commPort = portIdentifier.open(this.getClass().getName(), 2000);

	if (commPort instanceof SerialPort) {
	    serialPort = (SerialPort) commPort;
	    serialPort.setSerialPortParams(baudRate,
		    SerialPort.DATABITS_8,
		    SerialPort.STOPBITS_1,
		    SerialPort.PARITY_NONE);

	    InputStream in = serialPort.getInputStream();
	    serialPort.addEventListener(new SerialReader(in));
	    serialPort.notifyOnDataAvailable(true);
	}
	else { /* not a serial port */ }

    }

    public void releaseSerial()
    {
	if (serialPort != null) {
	    serialPort.removeEventListener();
	    serialPort.close();
	}
    }

    /**
     * Reads input from the hardware serial port.
     */
    private class SerialReader implements SerialPortEventListener
    {
	private InputStream in;

	public SerialReader(InputStream in)
	{
	    this.in = in;
	}

	public void serialEvent(SerialPortEvent ignore)
	{
	    int data;
	    byte[] buf = new byte[256];

	    try {
		int len = 0;
		while ((data = in.read()) > -1) {
		    if (data == '\n') {
			break;
		    }
		    buf[len++] = (byte) data;
		}

		buf[len++] = (byte) '\n';
		/* this probably should be done inside the EDT */
		jTextAreaSerial.append(new String(buf, 0, len));

	    }
	    catch (IOException e) {
		System.out.println(e.getMessage());
	    }
	}
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaSerial = new javax.swing.JTextArea();
        jPanelButtons = new javax.swing.JPanel();
        jButtonClear = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jCheckboxAutoscroll = new javax.swing.JCheckBox();
        jPanelHeader = new javax.swing.JPanel();
        jLabelInterface = new javax.swing.JLabel();
        jComboInterface = new javax.swing.JComboBox();
        jButtonConnect = new javax.swing.JToggleButton();

        setTitle("Serial monitor");

        jPanel.setLayout(new java.awt.BorderLayout());

        jTextAreaSerial.setColumns(20);
        jTextAreaSerial.setRows(5);
        jScrollPane1.setViewportView(jTextAreaSerial);

        jPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jButtonClear.setText("Clear");
        jButtonClear.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonClearActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonClear);

        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonClose);

        jCheckboxAutoscroll.setText("Auto scroll");
        jCheckboxAutoscroll.setActionCommand("Autoscroll");
        jCheckboxAutoscroll.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jCheckboxAutoscrollActionPerformed(evt);
            }
        });
        jPanelButtons.add(jCheckboxAutoscroll);

        jPanel.add(jPanelButtons, java.awt.BorderLayout.PAGE_END);

        jLabelInterface.setText("Interface");
        jPanelHeader.add(jLabelInterface);

        jComboInterface.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Virtual UART-0", "Physical interface" }));
        jPanelHeader.add(jComboInterface);

        jButtonConnect.setText("Connect");
        jButtonConnect.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonConnectActionPerformed(evt);
            }
        });
        jPanelHeader.add(jButtonConnect);

        jPanel.add(jPanelHeader, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonCloseActionPerformed
    {//GEN-HEADEREND:event_jButtonCloseActionPerformed
	setVisible(false);
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonClearActionPerformed
    {//GEN-HEADEREND:event_jButtonClearActionPerformed
	jTextAreaSerial.setText(new String());
    }//GEN-LAST:event_jButtonClearActionPerformed

    private class PtyReader implements Runnable
    {
	private FileInputStream fis;
	private boolean isCancelled = false;

	public PtyReader() throws FileNotFoundException
	{
	    // connect to simavr pty
	    fis = new FileInputStream("/tmp/simavr-uart0");
	}

	public void stop()
	{
	    isCancelled = true;
	}

	public void run()
	{
	    byte b;
	    try {
		while ((true) && (!isCancelled)) {
		    b = (byte) fis.read();
		    jTextAreaSerial.append(new String(new byte[]{b}));
		}
	    }
	    catch (IOException ex) {
		Logger.getLogger(SerialMonitorFrame.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

    private void jCheckboxAutoscrollActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckboxAutoscrollActionPerformed
    {//GEN-HEADEREND:event_jCheckboxAutoscrollActionPerformed

	if (jCheckboxAutoscroll.isSelected()) {
	    defCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    jTextAreaSerial.setCaretPosition(jTextAreaSerial.getDocument().getLength());
	}
	else {
	    defCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
	}
    }//GEN-LAST:event_jCheckboxAutoscrollActionPerformed

    private void jButtonConnectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonConnectActionPerformed
    {//GEN-HEADEREND:event_jButtonConnectActionPerformed
	try {
	    if (jButtonConnect.isSelected()) // Connect
	    {
		if (jComboInterface.getSelectedIndex() == HARD_INTERFACE) {
		    // Physical interface
		    connect("/dev/ttyUSB0", 9600);
		}
		else {
		    // Virtual interface
		    ptyWorker = new Thread(new PtyReader());
		    ptyWorker.start();
		}
		// if successfull change the button text
		jButtonConnect.setText("Disconnect");
		jComboInterface.setEnabled(false);
	    }
	    else // Disconnect
	    {
		if (jComboInterface.getSelectedIndex() == HARD_INTERFACE) {
		    releaseSerial();
		}
		else {
		    ptyWorker.stop();
		}

		jButtonConnect.setText("Connect");
		jComboInterface.setEnabled(true);
	    }
	}
	catch (Exception ex) {
	    JOptionPane.showMessageDialog(this, "Could not connect: " + ex.toString(),
		    "Error", JOptionPane.WARNING_MESSAGE);

	    Logger.getLogger(SerialMonitorFrame.class.getName()).log(
		    Level.SEVERE, null, ex);

	    jButtonConnect.setSelected(false);
	}

    }//GEN-LAST:event_jButtonConnectActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JToggleButton jButtonConnect;
    private javax.swing.JCheckBox jCheckboxAutoscroll;
    private javax.swing.JComboBox jComboInterface;
    private javax.swing.JLabel jLabelInterface;
    private javax.swing.JPanel jPanel;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaSerial;
    // End of variables declaration//GEN-END:variables
}
