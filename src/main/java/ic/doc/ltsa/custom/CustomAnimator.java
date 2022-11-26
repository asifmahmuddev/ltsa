package ic.doc.ltsa.custom;

import ic.doc.extension.Animator;
import ic.doc.extension.Relation;
import java.io.File;
import javax.swing.JFrame;

public abstract class CustomAnimator extends JFrame {
    public abstract void init(Animator paramAnimator, File paramFile, Relation paramRelation1, Relation paramRelation2, boolean paramBoolean);

    public abstract void stop();

    public void dispose() {
        stop();
        super.dispose();
    }
}
