package com.virtualino;

import com.ardublock.core.Context;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.virtualino.ui.StatusBar;
import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Manages Virtualino projects, including builds, firmware uploading, directory
 * tree creation and code generation.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class ProjectManager {
    
    private Context context;
    private Workspace workspace;

    private ProcessBuilder procBuilderIno;
    private ProcessBuilder procBuilderEmu;
    
    private Process emulatorProcess;

    public ProjectManager()
    {
        String path;
        procBuilderIno = new ProcessBuilder();
        procBuilderEmu = new ProcessBuilder();
        
        context = Context.getContext();
        workspace = context.getWorkspace();

        path = System.getProperty("java.io.tmpdir");
        path += "/virtualino";

        createProjectDirectory(path);

        procBuilderIno.directory(new File(path));
        procBuilderEmu.directory(new File("./"));
        
        File f = new File("./simduino.elf");
        boolean executableOkay = false;
        if (f.exists()) {
            if (f.canExecute()) {
                executableOkay = true;
            }
        }
        
        if  (!executableOkay) {
            JOptionPane.showMessageDialog(null,
                    "Emulator executable not found or not executable");
        }
        
        procBuilderEmu.command("./simduino.elf",
                path + "/.build/atmega328/firmware.elf");

        // init project directory
        inoCommand("init");
	
	// Adds a shutdown thread to kill the emulator when exiting
	Runtime.getRuntime().addShutdownHook(new Thread(new KillerThread()));
	
    }

    private void createProjectDirectory(String path) {
        File f = new File(path);
        if (!f.exists()) {
            boolean success = new File(path).mkdir();

            if (success) {
                System.out.println("Project directory " + path + " created");
            } else {
                System.out.println("Can't create project directory " + path);
            }
        }
    }

    private void inoCommand(String cmd) {
        String line;
        BufferedReader reader;
        InputStreamReader stream;

        if (cmd.equals("build") || cmd.equals("upload")) {
            procBuilderIno.command("ino", cmd, "-m", "atmega328");
        } else {
            procBuilderIno.command("ino", cmd);
        }

        // read the process output
        try {
            stream = new InputStreamReader(procBuilderIno.start().getInputStream());
            reader = new BufferedReader(stream);
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            stream.close();
        } catch (IOException ex) {
            Logger.getLogger(ProjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private class CodeGenThread implements Runnable
    {
	// generates code
	@Override
	public void run()
	{
	    String code = null;
	    boolean success = true;
	    
	    StatusBar status = StatusBar.getInstance();
	    status.setText("Translating..");

	    Translator translator = new Translator(workspace);
	    Set<RenderableBlock> loopBlockSet = new HashSet<RenderableBlock>();
	    Iterable<RenderableBlock> renderableBlocks = workspace.getRenderableBlocks();
	    
	    for (RenderableBlock renderableBlock : renderableBlocks) {
		Block block = renderableBlock.getBlock();

		if (!block.hasPlug() && (Block.NULL.equals(block.getBeforeBlockID()))) {
		    if (block.getGenusName().equals("loop")) {
			loopBlockSet.add(renderableBlock);
		    }

		}
	    }

	    if (loopBlockSet.isEmpty()) {
		JOptionPane.showMessageDialog(null, "No loop found!", "Error",
			JOptionPane.ERROR_MESSAGE);
		return;
	    }

	    if (loopBlockSet.size() > 1) {

		for (RenderableBlock rb : loopBlockSet) {
		    context.highlightBlock(rb);
		}

		JOptionPane.showMessageDialog(null, "Multiple loop blocks found",
			"Error", JOptionPane.ERROR_MESSAGE);
		return;
	    }


	    for (RenderableBlock renderableBlock : loopBlockSet) {

		Block loopBlock = renderableBlock.getBlock();

		try {
		    code = translator.translate(loopBlock.getBlockID());
		}
		catch (Exception ex) {

		    success = false;
		    String errorMsg = null;

		    Long blockId = null;
		    if (ex instanceof SocketNullException) {
			blockId = ((SocketNullException) ex).getBlockId();
			errorMsg = "A socket was left empty!";
		    }
		    else if (ex instanceof BlockException) {
			blockId = ((BlockException) ex).getBlockId();
			errorMsg = ex.getMessage();
		    }

		    // Highlight block
		    Iterable<RenderableBlock> blocks = workspace.getRenderableBlocks();
		    for (RenderableBlock renderableBlock2 : blocks) {
			Block block2 = renderableBlock2.getBlock();
			if (block2.getBlockID().equals(blockId)) {
			    context.highlightBlock(renderableBlock2);
			    break;
			}
		    }

		    JOptionPane.showMessageDialog(null,
			    errorMsg, "Error", JOptionPane.WARNING_MESSAGE);
		}
	    }

	    if (success) {
                
                // write the code to file
		try {
                    String path = System.getProperty("java.io.tmpdir");
                    path += "/virtualino/src/sketch.ino";
                    
                    FileWriter source = new FileWriter(path);
		    BufferedWriter out = new BufferedWriter(source);
		    out.write(code);
		    out.close();
		}
		catch (IOException ex) {
		    Logger.getLogger(ProjectManager.class.getName()).log(Level.SEVERE, null, ex);
		}
                
                System.out.println("done generating code");
                doBuildAndRun();
	    }
	}
    }

    private class BuilderThread implements Runnable {

        private StatusBar statusBar;

        public BuilderThread() {
            statusBar = StatusBar.getInstance();
        }

        public void run() {
            statusBar.setText("Building..");
            inoCommand("build");
            statusBar.setText("Running..");
            try {
                haltProgram(); // stops the emulator if running
                emulatorProcess = procBuilderEmu.start();
                System.out.println("Emulator process started");
            } catch (IOException ex) {
                Logger.getLogger(ProjectManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    /*
     * Builds and run the actual program.
     * This is basically a callback, invoked after the
     * code generation thread is finished
     */
    private void doBuildAndRun() {
        Thread t = new Thread(new BuilderThread());
        t.start();
    }
    
    /*
     * Generates the source code and saves it to a file,
     * then builds the source and runs the emulator.
     */
    public void buildAndRun() {
        Thread t = new Thread(new CodeGenThread());
        t.start();
    }
    
    private class KillerThread implements Runnable {

	public void run()
	{
	    haltProgram();
	}
    }

    public void haltProgram() {
        if (emulatorProcess != null) {
            emulatorProcess.destroy();
        }
    }
    
    public void buildProgram() {
        inoCommand("build");
    }
    
    public void uploadFirmware() {
        inoCommand("upload");
    }
}
