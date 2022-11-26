package org.jdom;

public class IllegalNameException extends IllegalArgumentException {
    private static final String CVS_ID = "@(#) $RCSfile: IllegalNameException.java,v $ $Revision: 1.7 $ $Date: 2002/01/08 09:17:10 $ $Name: jdom_1_0_b8 $";

    public IllegalNameException(String name, String construct, String reason) {
        super("The name \"" + name + "\" is not legal for JDOM/XML " + construct + "s: " + reason + ".");
    }

    public IllegalNameException(String name, String construct) {
        super("The name \"" + name + "\" is not legal for JDOM/XML " + construct + "s.");
    }
}
