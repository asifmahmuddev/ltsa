package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import uk.ac.ic.doc.scenebeans.cag.CAGProcessor;
import uk.ac.ic.doc.scenebeans.cag.SubtractProcessor;

public class Subtract extends CAGComposite {
    protected CAGProcessor newCAGProcessor(Graphics2D paramGraphics2D) {
        return (CAGProcessor) new SubtractProcessor(paramGraphics2D);
    }
}
