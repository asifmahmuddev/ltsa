package ic.doc.extension;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuBar;

public abstract class LTSAPlugin {
    private LTSA o_ltsa;

    final void setLTSA(LTSA paramLTSA) {
        this.o_ltsa = paramLTSA;
    }

    public final LTSA getLTSA() {
        return this.o_ltsa;
    }

    public LTSAPlugin() {
    }

    public LTSAPlugin(LTSA paramLTSA) {
        this.o_ltsa = paramLTSA;
        initialise();
    }

    public abstract String getName();

    public abstract boolean addAsTab();

    public Component getComponent() {
        return null;
    }

    public abstract boolean addToolbarButtons();

    public List getToolbarButtons() {
        return null;
    }

    public boolean providesOpenFile() {
        return false;
    }

    public void openFile(File paramFile) {
    }

    public boolean providesSaveFile() {
        return false;
    }

    public void saveFile(FileOutputStream paramFileOutputStream) {
    }

    public boolean providesNewFile() {
        return false;
    }

    public void newFile() {
    }

    public boolean providesCopy() {
        return false;
    }

    public void copy() {
    }

    public boolean providesCut() {
        return false;
    }

    public void cut() {
    }

    public boolean providesPaste() {
        return false;
    }

    public void paste() {
    }

    public String getFileExtension() {
        return null;
    }

    public boolean addMenuItems() {
        return false;
    }

    public Map getMenuItems() {
        return null;
    }

    public void initialise() {
    }

    public boolean addMenusToMenuBar() {
        return false;
    }

    public List getMenus() {
        return null;
    }

    public boolean useOwnMenuBar() {
        return false;
    }

    public JMenuBar getMenuBar() {
        return null;
    }
}
