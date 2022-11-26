package ic.doc.ltsa.custom;

import ic.doc.extension.Relation;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ControlActionRegistry {
    Hashtable actionNumber = new Hashtable();
    Hashtable controlNumber = new Hashtable();
    Relation actionsToControls;
    Relation controlsToActions;
    int[][] controlMap;
    int[][] actionMap;
    String[] actionAlphabet;
    String[] controlAlphabet;
    boolean[] controlState;
    AnimationMessage msg;

    public ControlActionRegistry(Relation paramRelation, AnimationMessage paramAnimationMessage) {
        this.actionsToControls = paramRelation;
        this.msg = paramAnimationMessage;
    }

    void getAnimatorControls() {
        byte b = 0;
        Vector vector = new Vector();
        Enumeration enumeration = this.controlsToActions.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            this.controlNumber.put(str, new Integer(b));
            vector.addElement(str);
            b++;
        }
        this.controlAlphabet = new String[b];
        vector.copyInto((Object[]) this.controlAlphabet);
    }

    public Vector getControls() {
        Vector vector = new Vector();
        Enumeration enumeration = this.actionsToControls.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            vector.addElement(str);
        }
        return vector;
    }

    public int controlled(String paramString) {
        Integer integer = (Integer) this.actionNumber.get(paramString);
        if (integer == null)
            return -1;
        return integer.intValue();
    }

    void initMap(String[] paramArrayOfString) {
        this.actionAlphabet = paramArrayOfString;
        for (byte b1 = 1; b1 < paramArrayOfString.length; b1++)
            this.actionNumber.put(paramArrayOfString[b1], new Integer(b1));
        Enumeration enumeration = this.actionsToControls.keys();
        Vector vector = new Vector();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            if (this.actionNumber.get(str) == null)
                vector.addElement(str);
        }
        enumeration = vector.elements();
        while (enumeration.hasMoreElements())
            this.actionsToControls.remove(enumeration.nextElement());
        this.controlsToActions = this.actionsToControls.inverse();
        getAnimatorControls();
        this.controlMap = new int[this.controlAlphabet.length][];
        this.controlState = new boolean[this.controlAlphabet.length];
        for (byte b2 = 0; b2 < this.controlState.length;) {
            this.controlState[b2] = true;
            b2++;
        }
        initControlMap();
        initActionMap();
    }

    protected void initControlMap() {
        Enumeration enumeration = this.controlsToActions.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            int i = ((Integer) this.controlNumber.get(str)).intValue();
            Object object = this.controlsToActions.get(str);
            if (object instanceof String) {
                int j = ((Integer) this.actionNumber.get(object)).intValue();
                int[] arrayOfInt1 = new int[1];
                arrayOfInt1[0] = j;
                this.controlMap[i] = arrayOfInt1;
                continue;
            }
            Vector vector = (Vector) object;
            int[] arrayOfInt = new int[vector.size()];
            Enumeration enumeration1 = vector.elements();
            byte b = 0;
            while (enumeration1.hasMoreElements()) {
                String str1 = enumeration1.nextElement();
                int j = ((Integer) this.actionNumber.get(str1)).intValue();
                arrayOfInt[b] = j;
                b++;
            }
            this.controlMap[i] = arrayOfInt;
        }
    }

    protected void initActionMap() {
        this.actionMap = new int[this.actionAlphabet.length][];
        Enumeration enumeration = this.actionsToControls.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            int i = ((Integer) this.actionNumber.get(str)).intValue();
            Object object = this.actionsToControls.get(str);
            if (object instanceof String) {
                int j = ((Integer) this.controlNumber.get(object)).intValue();
                int[] arrayOfInt1 = new int[1];
                arrayOfInt1[0] = j;
                this.actionMap[i] = arrayOfInt1;
                continue;
            }
            Vector vector = (Vector) object;
            int[] arrayOfInt = new int[vector.size()];
            Enumeration enumeration1 = vector.elements();
            byte b = 0;
            while (enumeration1.hasMoreElements()) {
                String str1 = enumeration1.nextElement();
                int j = ((Integer) this.controlNumber.get(str1)).intValue();
                arrayOfInt[b] = j;
                b++;
            }
            this.actionMap[i] = arrayOfInt;
        }
    }

    void print() {
        for (byte b = 0; b < this.controlMap.length; b++) {
            System.out.println(this.controlAlphabet[b]);
            for (byte b1 = 0; b1 < (this.controlMap[b]).length; b1++)
                System.out.print(" " + this.actionAlphabet[this.controlMap[b][b1]]);
            System.out.println();
        }
    }

    void mapControl(String paramString, boolean[] paramArrayOfboolean, boolean paramBoolean) {
        String str;
        if (paramBoolean) {
            str = "-enable-";
        } else {
            str = "-disabl-";
        }
        this.msg.debugMsg("-control" + str + paramString);
        Integer integer = (Integer) this.controlNumber.get(paramString);
        if (integer == null)
            return;
        int i = integer.intValue();
        this.controlState[i] = paramBoolean;
        if (this.controlMap[i] == null)
            return;
        for (byte b = 0; b < (this.controlMap[i]).length; b++) {
            int j = this.controlMap[i][b];
            if ((this.actionMap[j]).length == 1) {
                paramArrayOfboolean[j] = paramBoolean;
            } else {
                boolean bool = paramBoolean;
                for (byte b1 = 0; b1 < (this.actionMap[j]).length; b1++)
                    bool = (bool && this.controlState[this.actionMap[j][b1]]);
                paramArrayOfboolean[j] = bool;
            }
        }
    }
}
