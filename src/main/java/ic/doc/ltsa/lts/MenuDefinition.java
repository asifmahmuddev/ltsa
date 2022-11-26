package ic.doc.ltsa.lts;

import ic.doc.extension.Relation;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class MenuDefinition {
    Symbol name;
    ActionLabels actions;
    Symbol params;
    Symbol target;
    Vector actionMapDefn;
    Vector controlMapDefn;
    Vector animations;
    public static Hashtable definitions;

    public static void compile() {
        RunMenu.init();
        Enumeration enumeration = definitions.elements();
        while (enumeration.hasMoreElements()) {
            MenuDefinition menuDefinition = enumeration.nextElement();
            RunMenu.add(menuDefinition.makeRunMenu());
        }
    }

    public static String[] names() {
        if (definitions == null)
            return null;
        int i = definitions.size();
        if (i == 0)
            return null;
        String[] arrayOfString = new String[i];
        Enumeration enumeration = definitions.keys();
        byte b = 0;
        while (enumeration.hasMoreElements())
            arrayOfString[b++] = enumeration.nextElement();
        return arrayOfString;
    }

    public static boolean[] enabled(String paramString) {
        if (definitions == null)
            return null;
        int i = definitions.size();
        if (i == 0)
            return null;
        boolean[] arrayOfBoolean = new boolean[i];
        Enumeration enumeration = definitions.keys();
        byte b = 0;
        while (enumeration.hasMoreElements()) {
            MenuDefinition menuDefinition = (MenuDefinition) definitions.get(enumeration.nextElement());
            arrayOfBoolean[b++] = (menuDefinition.target == null) ? true : paramString.equals(menuDefinition.target.toString());
        }
        return arrayOfBoolean;
    }

    public RunMenu makeRunMenu() {
        String str = this.name.toString();
        if (this.params == null) {
            Vector vector = null;
            vector = this.actions.getActions(null, null);
            return new RunMenu(str, vector);
        }
        Relation relation1 = RelabelDefn.getRelabels(this.actionMapDefn);
        Relation relation2 = RelabelDefn.getRelabels(this.controlMapDefn);
        if (relation1 == null) {
            relation1 = new Relation();
        } else {
            relation1 = relation1.inverse();
        }
        if (relation2 == null) {
            relation2 = new Relation();
        } else {
            relation2 = relation2.inverse();
        }
        includeParts(relation1, relation2);
        return new RunMenu(str, (this.params == null) ? null : this.params.toString(), relation1, relation2);
    }

    protected void includeParts(Relation paramRelation1, Relation paramRelation2) {
        if (this.animations == null)
            return;
        Enumeration enumeration = this.animations.elements();
        while (enumeration.hasMoreElements()) {
            AnimationPart animationPart = enumeration.nextElement();
            animationPart.makePart();
            paramRelation1.union(animationPart.getActions());
            paramRelation2.union(animationPart.getControls());
        }
    }

    public void addAnimationPart(Symbol paramSymbol, Vector paramVector) {
        if (this.animations == null)
            this.animations = new Vector();
        this.animations.addElement(new AnimationPart(this, paramSymbol, paramVector));
    }

    class AnimationPart {
        Symbol name;
        Vector relabels;
        RunMenu compiled;
        private final MenuDefinition this$0;

        AnimationPart(MenuDefinition this$0, Symbol param1Symbol, Vector param1Vector) {
            this.this$0 = this$0;
            this.name = param1Symbol;
            this.relabels = param1Vector;
        }

        void makePart() {
            MenuDefinition menuDefinition = (MenuDefinition) MenuDefinition.definitions.get(this.name.toString());
            if (menuDefinition == null) {
                Diagnostics.fatal("Animation not found: " + this.name, this.name);
                return;
            }
            if (menuDefinition.params == null) {
                Diagnostics.fatal("Not an animation: " + this.name, this.name);
                return;
            }
            this.compiled = menuDefinition.makeRunMenu();
            if (this.relabels != null) {
                Relation relation = RelabelDefn.getRelabels(this.relabels);
                if (this.compiled.actions != null)
                    this.compiled.actions.relabel(relation);
                if (this.compiled.controls != null)
                    this.compiled.controls.relabel(relation);
            }
        }

        Relation getActions() {
            if (this.compiled != null)
                return this.compiled.actions;
            return null;
        }

        Relation getControls() {
            if (this.compiled != null)
                return this.compiled.controls;
            return null;
        }
    }
}
