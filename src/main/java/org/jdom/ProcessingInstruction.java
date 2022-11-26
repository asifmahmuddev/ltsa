package org.jdom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom.output.XMLOutputter;

public class ProcessingInstruction implements Serializable, Cloneable {
    private static final String CVS_ID = "@(#) $RCSfile: ProcessingInstruction.java,v $ $Revision: 1.27 $ $Date: 2002/03/20 15:16:32 $ $Name: jdom_1_0_b8 $";
    protected String target;
    protected String rawData;
    protected Map mapData;
    protected Object parent;

    protected ProcessingInstruction() {
    }

    public ProcessingInstruction(String target, Map data) {
        String reason;
        if ((reason = Verifier.checkProcessingInstructionTarget(target)) != null)
            throw new IllegalTargetException(target, reason);
        this.target = target;
        setData(data);
    }

    public ProcessingInstruction(String target, String data) {
        String reason;
        if ((reason = Verifier.checkProcessingInstructionTarget(target)) != null)
            throw new IllegalTargetException(target, reason);
        this.target = target;
        setData(data);
    }

    public Element getParent() {
        if (this.parent instanceof Element)
            return (Element) this.parent;
        return null;
    }

    protected ProcessingInstruction setParent(Element parent) {
        this.parent = parent;
        return this;
    }

    public ProcessingInstruction detach() {
        if (this.parent instanceof Element) {
            ((Element) this.parent).removeContent(this);
        } else if (this.parent instanceof Document) {
            ((Document) this.parent).removeContent(this);
        }
        return this;
    }

    public Document getDocument() {
        if (this.parent instanceof Document)
            return (Document) this.parent;
        if (this.parent instanceof Element)
            return ((Element) this.parent).getDocument();
        return null;
    }

    protected ProcessingInstruction setDocument(Document document) {
        this.parent = document;
        return this;
    }

    public String getTarget() {
        return this.target;
    }

    public String getData() {
        return this.rawData;
    }

    public List getNames() {
        Set mapDataSet = this.mapData.entrySet();
        List nameList = new ArrayList();
        for (Iterator i = mapDataSet.iterator(); i.hasNext();) {
            String wholeSet = i.next().toString();
            String attrName = wholeSet.substring(0, wholeSet.indexOf("="));
            nameList.add(attrName);
        }
        return nameList;
    }

    public ProcessingInstruction setData(String data) {
        this.rawData = data;
        this.mapData = parseData(data);
        return this;
    }

    public ProcessingInstruction setData(Map data) {
        this.rawData = toString(data);
        this.mapData = data;
        return this;
    }

    public String getValue(String name) {
        return (String) this.mapData.get(name);
    }

    public ProcessingInstruction setValue(String name, String value) {
        this.mapData.put(name, value);
        this.rawData = toString(this.mapData);
        return this;
    }

    public boolean removeValue(String name) {
        if (this.mapData.remove(name) != null) {
            this.rawData = toString(this.mapData);
            return true;
        }
        return false;
    }

    private String toString(Map mapData) {
        StringBuffer rawData = new StringBuffer();
        Iterator i = mapData.keySet().iterator();
        while (i.hasNext()) {
            String name = i.next();
            String value = (String) mapData.get(name);
            rawData.append(name).append("=\"").append(value).append("\" ");
        }
        rawData.setLength(rawData.length() - 1);
        return rawData.toString();
    }

    private Map parseData(String rawData) {
        Map data = new HashMap();
        String inputData = rawData.trim();
        while (!inputData.trim().equals("")) {
            String name = "";
            String value = "";
            int startName = 0;
            char previousChar = inputData.charAt(startName);
            int pos = 1;
            for (; pos < inputData.length(); pos++) {
                char currentChar = inputData.charAt(pos);
                if (currentChar == '=') {
                    name = inputData.substring(startName, pos).trim();
                    value = extractQuotedString(inputData.substring(pos + 1).trim());
                    if (value == null)
                        return new HashMap();
                    pos += value.length() + 1;
                    break;
                }
                if (Character.isWhitespace(previousChar) && !Character.isWhitespace(currentChar))
                    startName = pos;
                previousChar = currentChar;
            }
            inputData = inputData.substring(pos);
            if (name.length() > 0 && value != null)
                data.put(name, value);
        }
        return data;
    }

    private String extractQuotedString(String rawData) {
        boolean inQuotes = false;
        char quoteChar = '"';
        int start = 0;
        for (int pos = 0; pos < rawData.length(); pos++) {
            char currentChar = rawData.charAt(pos);
            if (currentChar == '"' || currentChar == '\'')
                if (!inQuotes) {
                    quoteChar = currentChar;
                    inQuotes = true;
                    start = pos + 1;
                } else if (quoteChar == currentChar) {
                    inQuotes = false;
                    return rawData.substring(start, pos);
                }
        }
        return null;
    }

    public String toString() {
        return "[ProcessingInstruction: " + (new XMLOutputter()).outputString(this) + "]";
    }

    public final boolean equals(Object ob) {
        return !(ob != this);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public Object clone() {
        ProcessingInstruction pi = null;
        try {
            pi = (ProcessingInstruction) super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
        }
        pi.parent = null;
        if (this.mapData != null)
            pi.mapData = parseData(this.rawData);
        return pi;
    }
}
