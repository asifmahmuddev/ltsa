package org.jdom;

public class IllegalDataException extends IllegalArgumentException {
    private static final String CVS_ID = "@(#) $RCSfile: IllegalDataException.java,v $ $Revision: 1.7 $ $Date: 2002/01/08 09:17:10 $ $Name: jdom_1_0_b8 $";

    public IllegalDataException(String data, String construct, String reason) {
        super("The data \"" + data + "\" is not legal for a JDOM " + construct + ": " + reason + ".");
    }

    public IllegalDataException(String data, String construct) {
        super("The data \"" + data + "\" is not legal for a JDOM " + construct + ".");
    }
}
