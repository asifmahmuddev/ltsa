package org.jdom;

public class IllegalTargetException extends IllegalArgumentException {
    private static final String CVS_ID = "@(#) $RCSfile: IllegalTargetException.java,v $ $Revision: 1.8 $ $Date: 2002/01/08 09:17:10 $ $Name: jdom_1_0_b8 $";

    public IllegalTargetException(String target, String reason) {
        super("The target \"" + target + "\" is not legal for JDOM/XML Processing Instructions: " + reason + ".");
    }

    public IllegalTargetException(String target) {
        super("The name \"" + target + "\" is not legal for JDOM/XML Processing Instructions.");
    }
}
