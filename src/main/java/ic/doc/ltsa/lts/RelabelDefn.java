package ic.doc.ltsa.lts;

import ic.doc.extension.Relation;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class RelabelDefn {
    ActionLabels newlabel;
    ActionLabels oldlabel;
    ActionLabels range;
    Vector defns;

    public void makeRelabels(Hashtable paramHashtable, Relation paramRelation) {
        Hashtable hashtable = new Hashtable();
        mkRelabels(paramHashtable, hashtable, paramRelation);
    }

    public void makeRelabels(Hashtable paramHashtable1, Hashtable paramHashtable2, Relation paramRelation) {
        mkRelabels(paramHashtable1, paramHashtable2, paramRelation);
    }

    private void mkRelabels(Hashtable paramHashtable1, Hashtable paramHashtable2, Relation paramRelation) {
        if (this.range != null) {
            this.range.initContext(paramHashtable2, paramHashtable1);
            while (this.range.hasMoreNames()) {
                this.range.nextName();
                Enumeration enumeration = this.defns.elements();
                while (enumeration.hasMoreElements()) {
                    RelabelDefn relabelDefn = enumeration.nextElement();
                    relabelDefn.mkRelabels(paramHashtable1, paramHashtable2, paramRelation);
                }
            }
            this.range.clearContext();
        } else {
            this.newlabel.initContext(paramHashtable2, paramHashtable1);
            while (this.newlabel.hasMoreNames()) {
                String str = this.newlabel.nextName();
                this.oldlabel.initContext(paramHashtable2, paramHashtable1);
                while (this.oldlabel.hasMoreNames()) {
                    String str1 = this.oldlabel.nextName();
                    paramRelation.put(str1, str);
                }
            }
            this.newlabel.clearContext();
        }
    }

    public static Relation getRelabels(Vector paramVector, Hashtable paramHashtable1, Hashtable paramHashtable2) {
        if (paramVector == null)
            return null;
        Relation relation = new Relation();
        Enumeration enumeration = paramVector.elements();
        while (enumeration.hasMoreElements()) {
            RelabelDefn relabelDefn = enumeration.nextElement();
            relabelDefn.makeRelabels(paramHashtable1, paramHashtable2, relation);
        }
        return relation;
    }

    public static Relation getRelabels(Vector paramVector) {
        return getRelabels(paramVector, new Hashtable(), new Hashtable());
    }
}
