package ic.doc.extension;

import ic.doc.ltsa.lts.LTSOutput;
import java.util.List;
import java.util.Vector;
import javax.swing.JEditorPane;
import javax.swing.undo.UndoManager;

public interface LTSA extends LTSOutput {
    void compileNoClear();

    UndoManager getUndoManager();

    JEditorPane getInputPane();

    void setTargetChoice(String paramString);

    void showOutput();

    boolean parse();

    void invalidateState();

    void updateDoState();

    boolean isCurrentStateNull();

    boolean isCurrentStateComposed();

    void composeCurrentState();

    void analyseCurrentState();

    Vector getCurrentStateErrorTrace();

    void postCurrentState();

    void swapto(String paramString);

    void exportGraphic(Exportable paramExportable);

    Animator getAnimator();

    String getCurrentDirectory();

    void setCurrentDirectory(String paramString);

    List getLTSNames();
}
