package com.virtualino.ui;

import com.ardublock.core.Context;
import com.virtualino.PinController;
import com.virtualino.ProjectManager;
import com.virtualino.components.*;
import com.virtualino.exception.NoMorePinsException;
import com.virtualino.net.NetCom;
import com.virtualino.ui.listener.LoadButtonListener;
import com.virtualino.ui.listener.SaveButtonListener;
import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.FactoryRenderableBlock;
import edu.mit.blocks.workspace.Workspace;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * Main Frame for Virtualino. Contains both the simulation area and the
 * programming interface (Ardublock).
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class VirtualinoFrame extends javax.swing.JFrame
{
    private StatusBar statusBar;
    private EmulatorFrame emulatorFrame;
    
    private InstructionsFrame instructionsFrame;
    private SerialMonitorFrame serialMonitor;
    
    private ProjectManager projectMng;

    private boolean isEmulationRunning;
    private boolean isComponentBeingDragged;
    
    private Cursor cursorOpenHand;
    private Cursor cursorClosedHand;
    
    //private HashMap<String, FactoryRenderableBlock> blockFactories;

    public VirtualinoFrame()
    {
	initComponents();
	setTitle("Virtualino");
        
        projectMng = new ProjectManager();
        emulatorFrame = new EmulatorFrame();
	
	statusBar = StatusBar.getInstance();
	
	jToolBar.add(statusBar, -1);
	jSplitPane1.setBottomComponent(Context.getContext().getWorkspace());
	jSplitPane2.setRightComponent(emulatorFrame);
        
	// select 'input' tab
        jTabbedPane1.setSelectedIndex(1);

	isEmulationRunning = false;
	isComponentBeingDragged = false;
	
	// load a default workspace if it exists
	File defaultProject = new File ("./default.blocks");
	if (defaultProject.exists()) {
	    try {
		Context.getContext().loadArduBlockFile(defaultProject);
	    }
	    catch (IOException ex) {
		Logger.getLogger(VirtualinoFrame.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

	/* Load mouse cursors to be displayed while hovering
	 * component's palette
	 */
	try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            
	    Image imgOpenHand = ImageIO.read(getClass().getResourceAsStream(
		    "/com/virtualino/ui/open-hand.gif"));
	    Image imgClosedHand = ImageIO.read(getClass().getResourceAsStream(
		    "/com/virtualino/ui/closed-hand.gif"));

	    cursorOpenHand = toolkit.createCustomCursor(imgOpenHand, new Point(0, 0), "cursor");
	    cursorClosedHand = toolkit.createCustomCursor(imgClosedHand, new Point(0, 0), "cursor");
	}
	catch (IOException ex) {
	    Logger.getLogger(VirtualinoFrame.class.getName()).log(Level.SEVERE, null, ex);
	}

	// Init socket connection
	NetCom.getInstance().startListening();
        
        LoadButtonListener loadAction = new LoadButtonListener(this);
        SaveButtonListener saveAction = new SaveButtonListener(emulatorFrame);
	
	jButtonLoad.addActionListener(loadAction);
	jButtonSave.addActionListener(saveAction);
        jMenuItemLoad.addActionListener(loadAction);
        jMenuItemSave.addActionListener(saveAction);
	
	// center & show 
        setLocationRelativeTo(null);
	setVisible(true);
    }

    /*
     * Adds the component to the simulation canvas and creates
     * the corresponding Block (FactoryRenderableBlock) in Ardublock's
     * drawer
     */
    public void addVirtualinoComponent(GenericComponent component)
    {
	Context c = Context.getContext();
	Workspace w = c.getWorkspace();

	emulatorFrame.add(component);
	emulatorFrame.repaint();

	Block b = new Block(w, component.toString(), component.getName());

	// create the block factory
	FactoryRenderableBlock frb = new FactoryRenderableBlock(w,
		w.getFactoryManager(), b.getBlockID());

	//blockFactories.put(component.getName(), frb);

	// add the block factory to Virtualino's drawer          
	w.getFactoryManager().addStaticBlock(frb, "Virtualino");

    }

    /* not used */
    public void removeVirtualinoComponent(String componentName)
    {
	Context c = Context.getContext();
	Workspace w = c.getWorkspace();

	//FactoryRenderableBlock frb = blockFactories.get(componentName);
	//w.getFactoryManager().removeStaticBlock(frb, "Virtualino");

	//blockFactories.remove(componentName);
    }

    public String askForComponentName()
    {
	String name = null;
	PinController pc = new PinController();

	boolean userCanceled = false;
	boolean isNameGood = false;

	while (!isNameGood && !userCanceled) {
	    name = JOptionPane.showInputDialog("Enter component name");

	    if (name == null) {
		userCanceled = true;
		continue;
	    }
	    else {
		isNameGood = !pc.findComponent(name) && !name.isEmpty();
	    }

	    if (!isNameGood) {
		JOptionPane.showMessageDialog(this, "Name is invalid or"
						    + " already in use, try again.");
	    }
	}

	return name;
    }

    public void resetWorkspace()
    {
        Context con = Context.getContext();
        PinController pinController = new PinController();
        
        // remove all components
        for (Component c : emulatorFrame.getComponents()) {
            pinController.removeComponent(c.getName());
            emulatorFrame.remove(c);
        }
        
        // clear all registered pins
        pinController.clearPins();
        
        // remove all blocks
        con.getWorkspaceController().loadFreshWorkspace();
    }
    
    private void jButtonUploadActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonUploadActionPerformed
    {//GEN-HEADEREND:event_jButtonUploadActionPerformed
	if (serialMonitor != null) {
	    serialMonitor.releaseSerial();
	}

	projectMng.uploadFirmware();
    }//GEN-LAST:event_jButtonUploadActionPerformed

    private void jButtonSerialActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonSerialActionPerformed
    {//GEN-HEADEREND:event_jButtonSerialActionPerformed
	if (serialMonitor == null) {
	    serialMonitor = new SerialMonitorFrame();
	}
	serialMonitor.setVisible(true);
    }//GEN-LAST:event_jButtonSerialActionPerformed

    private void paletteMouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_paletteMouseEntered
    {//GEN-HEADEREND:event_paletteMouseEntered
	((Component) evt.getSource()).setCursor(cursorOpenHand);
    }//GEN-LAST:event_paletteMouseEntered

    private void paletteMouseExited(java.awt.event.MouseEvent evt)//GEN-FIRST:event_paletteMouseExited
    {//GEN-HEADEREND:event_paletteMouseExited
	if (isComponentBeingDragged == false) {
	    ((Component) evt.getSource()).setCursor(Cursor.getDefaultCursor());
	}
    }//GEN-LAST:event_paletteMouseExited

    private void paletteMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_paletteMousePressed
    {//GEN-HEADEREND:event_paletteMousePressed
	((Component) evt.getSource()).setCursor(cursorClosedHand);
    }//GEN-LAST:event_paletteMousePressed

    /*
     * Handles the draggin of palette items (components) on the canvas,
     * allocating the appropriate instance of the component.
     */
    private void paletteMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_paletteMouseReleased
    {//GEN-HEADEREND:event_paletteMouseReleased
	// change cursor settings
	isComponentBeingDragged = false;
	((Component) evt.getSource()).setCursor(Cursor.getDefaultCursor());

	int x = evt.getXOnScreen() - emulatorFrame.getLocationOnScreen().x;
	int y = evt.getYOnScreen() - emulatorFrame.getLocationOnScreen().y;

	if (emulatorFrame.contains(x, y)) {

	    String componentName;
	    GenericComponent component = null;

	    Object source = evt.getSource();
	    componentName = askForComponentName();

	    // user hit cancel
	    if (componentName == null) {
		return;
	    }

	    try {
		if (source.equals(jLabelButton)) {
		    component = new ButtonComponent(componentName);
		}
		else if (source.equals(jLabelSlider)) {
		    component = new SliderComponent(componentName);
		}
		else if (source.equals(jLabelLDR)) {
		    component = new LDRComponent(componentName);
		}
		else if (source.equals(jLabelBuzzer)) {
		    component = new BuzzerComponent(componentName);
		}
		else if (source.equals(jLabelTemperature)) {
		    component = new TemperatureComponent(componentName);
		}
		else if (source.equals(jLabelYellowLed)) {
		    component = new YellowLedComponent(componentName);
		}
		else if (source.equals(jLabelGreenLed)) {
		    component = new GreenLedComponent(componentName);
		}
		else if (source.equals(jLabelRedLed)) {
		    component = new RedLedComponent(componentName);
		}
		else if (source.equals(jLabelRedLedAnalog)) {
		    component = new AnalogLedComponent(componentName, Color.red);
		}
		else if (source.equals(jLabelGreenLedAnalog)) {
		    component = new AnalogLedComponent(componentName, Color.green);
		}
		else if (source.equals(jLabelYellowLedAnalog)) {
		    component = new AnalogLedComponent(componentName, Color.yellow);
		}
		else if (source.equals(jLabelRGB)) {
		    component = new RGBLedComponent(componentName);
		}

		component.setLocation(x, y);
		addVirtualinoComponent(component);

	    }
	    catch (NoMorePinsException ex) {
		JOptionPane.showMessageDialog(this, ex.getMessage());
	    }
	}
    }//GEN-LAST:event_paletteMouseReleased

    private void jMenuItemNewActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItemNewActionPerformed
    {//GEN-HEADEREND:event_jMenuItemNewActionPerformed
        resetWorkspace();
    }//GEN-LAST:event_jMenuItemNewActionPerformed

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jToolBar = new javax.swing.JToolBar();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jButtonLoad = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonEmulate = new javax.swing.JButton();
        jButtonUpload = new javax.swing.JButton();
        jButtonSerial = new javax.swing.JButton();
        jButtonInstructions = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelInput = new javax.swing.JPanel();
        jLabelButton = new javax.swing.JLabel();
        jLabelSlider = new javax.swing.JLabel();
        jLabelLDR = new javax.swing.JLabel();
        jLabelTemperature = new javax.swing.JLabel();
        jPanelOutput = new javax.swing.JPanel();
        jLabelRedLed = new javax.swing.JLabel();
        jLabelGreenLed = new javax.swing.JLabel();
        jLabelYellowLed = new javax.swing.JLabel();
        jLabelRedLedAnalog = new javax.swing.JLabel();
        jLabelGreenLedAnalog = new javax.swing.JLabel();
        jLabelYellowLedAnalog = new javax.swing.JLabel();
        jLabelRGB = new javax.swing.JLabel();
        jLabelBuzzer = new javax.swing.JLabel();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNew = new javax.swing.JMenuItem();
        jMenuItemLoad = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(640, 640));

        jToolBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar.setRollover(true);
        jToolBar.add(filler2);

        jButtonLoad.setText("load project");
        jButtonLoad.setFocusable(false);
        jToolBar.add(jButtonLoad);

        jButtonSave.setText("save project");
        jButtonSave.setFocusable(false);
        jToolBar.add(jButtonSave);

        jButtonEmulate.setText("emulate");
        jButtonEmulate.setFocusable(false);
        jButtonEmulate.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonEmulateActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonEmulate);

        jButtonUpload.setText("upload");
        jButtonUpload.setFocusable(false);
        jButtonUpload.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonUploadActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonUpload);

        jButtonSerial.setText("serial monitor");
        jButtonSerial.setFocusable(false);
        jButtonSerial.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonSerialActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonSerial);

        jButtonInstructions.setText("instructions");
        jButtonInstructions.setFocusable(false);
        jButtonInstructions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonInstructions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonInstructions.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonInstructionsActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonInstructions);
        jToolBar.add(filler1);

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jSplitPane2.setMinimumSize(new java.awt.Dimension(600, 250));

        jPanelInput.setLayout(new javax.swing.BoxLayout(jPanelInput, javax.swing.BoxLayout.Y_AXIS));

        jLabelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/virtualino/ui/button16.png"))); // NOI18N
        jLabelButton.setText("Button");
        jLabelButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                paletteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                paletteMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                paletteMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                paletteMouseEntered(evt);
            }
        });
        jPanelInput.add(jLabelButton);

        jLabelSlider.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/virtualino/ui/slider16.png"))); // NOI18N
        jLabelSlider.setText("Slider / Pot.");
        jLabelSlider.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                paletteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                paletteMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                paletteMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                paletteMouseEntered(evt);
            }
        });
        jPanelInput.add(jLabelSlider);

        jLabelLDR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/virtualino/ui/ldr16.png"))); // NOI18N
        jLabelLDR.setText("Light sensor");
        jLabelLDR.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                paletteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                paletteMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                paletteMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                paletteMouseEntered(evt);
            }
        });
        jPanelInput.add(jLabelLDR);

        jLabelTemperature.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/virtualino/ui/temperature16.png"))); // NOI18N
        jLabelTemperature.setText("Temperature s.");
        jLabelTemperature.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                paletteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                paletteMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                paletteMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                paletteMouseEntered(evt);
            }
        });
        jPanelInput.add(jLabelTemperature);

        jTabbedPane1.addTab("Input", jPanelInput);

        jPanelOutput.setLayout(new javax.swing.BoxLayout(jPanelOutput, javax.swing.BoxLayout.Y_AXIS));

        jLabelRedLed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/virtualino/ui/led-red-16.png"))); // NOI18N
        jLabelRedLed.setText("Red Led");
        jLabelRedLed.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                paletteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                paletteMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                paletteMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                paletteMouseEntered(evt);
            }
        });
        jPanelOutput.add(jLabelRedLed);

        jLabelGreenLed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/virtualino/ui/led-green-16.png"))); // NOI18N
        jLabelGreenLed.setText("Green Led ");
        jLabelGreenLed.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                paletteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                paletteMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                paletteMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                paletteMouseEntered(evt);
            }
        });
        jPanelOutput.add(jLabelGreenLed);

        jLabelYellowLed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/virtualino/ui/led-yellow-16.png"))); // NOI18N
        jLabelYellowLed.setText("Yellow Led");
        jLabelYellowLed.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                paletteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                paletteMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                paletteMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                paletteMouseEntered(evt);
            }
        });
        jPanelOutput.add(jLabelYellowLed);

        jLabelRedLedAnalog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/virtualino/ui/led-red-16.png"))); // NOI18N
        jLabelRedLedAnalog.setText("Red Led (PWM)");
        jLabelRedLedAnalog.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                paletteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                paletteMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                paletteMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                paletteMouseEntered(evt);
            }
        });
        jPanelOutput.add(jLabelRedLedAnalog);

        jLabelGreenLedAnalog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/virtualino/ui/led-green-16.png"))); // NOI18N
        jLabelGreenLedAnalog.setText("Green Led (PWM)");
        jLabelGreenLedAnalog.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                paletteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                paletteMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                paletteMouseExited(evt);
            }
        });
        jLabelGreenLedAnalog.addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
        {
            public void mouseMoved(java.awt.event.MouseEvent evt)
            {
                paletteMouseEntered(evt);
            }
        });
        jPanelOutput.add(jLabelGreenLedAnalog);

        jLabelYellowLedAnalog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/virtualino/ui/led-yellow-16.png"))); // NOI18N
        jLabelYellowLedAnalog.setText("Yellow Led (PWM)");
        jLabelYellowLedAnalog.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                paletteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                paletteMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                paletteMouseExited(evt);
            }
        });
        jLabelYellowLedAnalog.addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
        {
            public void mouseMoved(java.awt.event.MouseEvent evt)
            {
                paletteMouseEntered(evt);
            }
        });
        jPanelOutput.add(jLabelYellowLedAnalog);

        jLabelRGB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/virtualino/ui/led-rgb-16.png"))); // NOI18N
        jLabelRGB.setText("Led (RGB)");
        jLabelRGB.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                paletteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                paletteMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                paletteMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                paletteMouseEntered(evt);
            }
        });
        jPanelOutput.add(jLabelRGB);

        jLabelBuzzer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/virtualino/ui/speaker16.png"))); // NOI18N
        jLabelBuzzer.setText("Buzzer");
        jLabelBuzzer.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                paletteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                paletteMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                paletteMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                paletteMouseEntered(evt);
            }
        });
        jPanelOutput.add(jLabelBuzzer);

        jTabbedPane1.addTab("Output", jPanelOutput);

        jSplitPane2.setLeftComponent(jTabbedPane1);

        jSplitPane1.setTopComponent(jSplitPane2);

        jMenuFile.setText("File");

        jMenuItemNew.setText("New project");
        jMenuItemNew.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemNewActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNew);

        jMenuItemLoad.setText("Load project");
        jMenuFile.add(jMenuItemLoad);

        jMenuItemSave.setText("Save project");
        jMenuFile.add(jMenuItemSave);
        jMenuFile.add(jSeparator1);

        jMenuItemExit.setText("Quit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar.add(jMenuFile);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonEmulateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonEmulateActionPerformed
    {//GEN-HEADEREND:event_jButtonEmulateActionPerformed
	if (!isEmulationRunning) {
            isEmulationRunning = true;
            statusBar.setText("Translating..");
            jButtonEmulate.setText("stop");
            projectMng.buildAndRun();
        }
        else {
            isEmulationRunning = false;
            statusBar.setStatusMessage("Stopped");
            jButtonEmulate.setText("emulate");
            PinController p = new PinController();
            p.zeroPins();
            projectMng.haltProgram();
	}
    }//GEN-LAST:event_jButtonEmulateActionPerformed

    private void jButtonInstructionsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonInstructionsActionPerformed
    {//GEN-HEADEREND:event_jButtonInstructionsActionPerformed
        instructionsFrame = new InstructionsFrame(emulatorFrame.printInstructions());
        instructionsFrame.setVisible(true);
    }//GEN-LAST:event_jButtonInstructionsActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItemExitActionPerformed
    {//GEN-HEADEREND:event_jMenuItemExitActionPerformed
	if (isEmulationRunning) {
	    projectMng.haltProgram();
	}
	System.exit(0);
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton jButtonEmulate;
    private javax.swing.JButton jButtonInstructions;
    private javax.swing.JButton jButtonLoad;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSerial;
    private javax.swing.JButton jButtonUpload;
    private javax.swing.JLabel jLabelButton;
    private javax.swing.JLabel jLabelBuzzer;
    private javax.swing.JLabel jLabelGreenLed;
    private javax.swing.JLabel jLabelGreenLedAnalog;
    private javax.swing.JLabel jLabelLDR;
    private javax.swing.JLabel jLabelRGB;
    private javax.swing.JLabel jLabelRedLed;
    private javax.swing.JLabel jLabelRedLedAnalog;
    private javax.swing.JLabel jLabelSlider;
    private javax.swing.JLabel jLabelTemperature;
    private javax.swing.JLabel jLabelYellowLed;
    private javax.swing.JLabel jLabelYellowLedAnalog;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemLoad;
    private javax.swing.JMenuItem jMenuItemNew;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JPanel jPanelInput;
    private javax.swing.JPanel jPanelOutput;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar;
    // End of variables declaration//GEN-END:variables
}
