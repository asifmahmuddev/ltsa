package ic.doc.ltsa.lts;

import ic.doc.extension.Relation;
import java.util.Hashtable;
import java.util.Vector;

public class RunMenu {
    public String name;
    public Vector alphabet;
    public String params;
    public Relation actions;
    public Relation controls;
    public static Hashtable menus;

    public static void init() {
        menus = new Hashtable();
    }

    public RunMenu(String paramString1, String paramString2, Relation paramRelation1, Relation paramRelation2) {
        this.name = paramString1;
        this.params = paramString2;
        this.actions = paramRelation1;
        this.controls = paramRelation2;
    }

    public RunMenu(String paramString, Vector paramVector) {
        this.name = paramString;
        this.alphabet = paramVector;
    }

    public static void add(RunMenu paramRunMenu) {
        menus.put(paramRunMenu.name, paramRunMenu);
    }

    public boolean isCustom() {
        return (this.params != null);
    }
}
