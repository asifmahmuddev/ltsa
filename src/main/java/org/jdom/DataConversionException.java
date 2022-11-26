package org.jdom;

public class DataConversionException extends JDOMException {
    private static final String CVS_ID = "@(#) $RCSfile: DataConversionException.java,v $ $Revision: 1.7 $ $Date: 2002/01/08 09:17:10 $ $Name: jdom_1_0_b8 $";

    public DataConversionException(String name, String dataType) {
        super("The XML construct " + name + " could not be converted to a " + dataType);
    }
}
