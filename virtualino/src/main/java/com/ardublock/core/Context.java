package com.ardublock.core;

//import com.ardublock.ui.listener.OpenblocksFrameListener;
import edu.mit.blocks.controller.WorkspaceController;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class Context
{
    private Workspace workspace;
    private WorkspaceController workspaceController;
    
    private static Context singletonContext;
    
    private boolean workspaceChanged;
    private Set<RenderableBlock> highlightBlockSet;
    //private Set<OpenblocksFrameListener> ofls;
    
    public final static String LANG_DTD_PATH = "/com/ardublock/block/lang_def.dtd";
    public final static String ARDUBLOCK_LANG_PATH = "/com/ardublock/block/ardublock.xml";

    public static Context getContext()
    {
        synchronized (Context.class) {
            if (singletonContext == null) {
                singletonContext = new Context();
            }
        }
	return singletonContext;
    }

    private Context()
    {
	workspaceChanged = false;
	workspaceController = new WorkspaceController();
	workspaceController.resetWorkspace();
	workspaceController.resetLanguage();
	workspaceController.setLangResourceBundle(ResourceBundle.getBundle("com/ardublock/block/ardublock"));
	//workspaceController.setStyleList(list);
	workspaceController.setLangDefDtd(this.getClass().getResourceAsStream(LANG_DTD_PATH));
	workspaceController.setLangDefStream(this.getClass().getResourceAsStream(ARDUBLOCK_LANG_PATH));
	workspaceController.loadFreshWorkspace();
	workspace = workspaceController.getWorkspace();
	highlightBlockSet = new HashSet<RenderableBlock>();
	//ofls = new HashSet<OpenblocksFrameListener>();
	this.workspace = workspaceController.getWorkspace();
    }

    public WorkspaceController getWorkspaceController()
    {
	return workspaceController;
    }

    public Workspace getWorkspace()
    {
	return workspace;
    }

    public boolean isWorkspaceChanged()
    {
	return workspaceChanged;
    }

    public void setWorkspaceChanged(boolean workspaceChanged)
    {
	this.workspaceChanged = workspaceChanged;
    }

    public void highlightBlock(RenderableBlock block)
    {
	block.updateInSearchResults(true);
	highlightBlockSet.add(block);
    }

    public void cancelHighlightBlock(RenderableBlock block)
    {
	block.updateInSearchResults(false);
	highlightBlockSet.remove(block);
    }

    public void resetHightlightBlock()
    {
	for (RenderableBlock rb : highlightBlockSet) {
	    rb.updateInSearchResults(false);
	}
	highlightBlockSet.clear();
    }

    public void saveArduBlockFile(File saveFile, String saveString) throws IOException
    {
	if (!saveFile.exists()) {
	    saveFile.createNewFile();
	}
	FileOutputStream fos = new FileOutputStream(saveFile, false);
	fos.write(saveString.getBytes("UTF8"));
	fos.flush();
	fos.close();
	//didSave();
    }

    public void loadArduBlockFile(File savedFile) throws IOException
    {
	if (savedFile != null) {
	    String saveFilePath = savedFile.getAbsolutePath();
	    workspaceController.resetWorkspace();
	    workspaceController.loadProjectFromPath(saveFilePath);
	    //didLoad();
	}
    }

    /*public void registerOpenblocksFrameListener(OpenblocksFrameListener ofl)
    {
	ofls.add(ofl);
    }

    public void didSave()
    {
	for (OpenblocksFrameListener ofl : ofls) {
	    ofl.didSave();
	}
    }

    public void didLoad()
    {
	for (OpenblocksFrameListener ofl : ofls) {
	    ofl.didLoad();
	}
    }

    public void didGenerate(String sourcecode)
    {
	for (OpenblocksFrameListener ofl : ofls) {
	    ofl.didGenerate(sourcecode);
	}
    }*/
}
