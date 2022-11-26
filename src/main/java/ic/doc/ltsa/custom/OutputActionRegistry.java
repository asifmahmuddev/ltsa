package ic.doc.ltsa.custom;

import ic.doc.extension.Relation;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class OutputActionRegistry {
    Hashtable outputs = new Hashtable();
    Relation actionMap;
    AnimationMessage msg;

    public OutputActionRegistry(Relation paramRelation, AnimationMessage paramAnimationMessage) {
        this.actionMap = paramRelation;
        this.msg = paramAnimationMessage;
    }

    public void register(String paramString, AnimationAction paramAnimationAction) {
        Vector vector = (Vector) this.outputs.get(paramString);
        if (vector != null) {
            vector.addElement(paramAnimationAction);
        } else {
            vector = new Vector();
            vector.addElement(paramAnimationAction);
            this.outputs.put(paramString, vector);
        }
    }

    public void doAction(String paramString) {
        this.msg.traceMsg(paramString);
        Object object = this.actionMap.get(paramString);
        if (object == null)
            return;
        if (object instanceof String) {
            execute((String) object);
        } else {
            Vector vector = (Vector) object;
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements())
                execute(enumeration.nextElement());
        }
    }

    private void execute(String paramString) {
        this.msg.debugMsg("-action -" + paramString);
        Vector vector = (Vector) this.outputs.get(paramString);
        if (vector == null)
            return;
        Enumeration enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            AnimationAction animationAction = enumeration.nextElement();
            animationAction.action();
        }
    }
}
