package ic.doc.ltsa.editor;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.ViewFactory;

public class ColoredEditorKit extends DefaultEditorKit {
    private ColoredContext d_Preferences = new ColoredContext();

    public void setStylePreferences(ColoredContext paramColoredContext) {
        this.d_Preferences = paramColoredContext;
    }

    public ColoredContext getStylePreferences() {
        return this.d_Preferences;
    }

    public final ViewFactory getViewFactory() {
        return getStylePreferences();
    }

    public String getContentType() {
        return "text/lts";
    }

    public Object clone() {
        ColoredEditorKit coloredEditorKit = new ColoredEditorKit();
        coloredEditorKit.d_Preferences = this.d_Preferences;
        return coloredEditorKit;
    }

    public Document createDefaultDocument() {
        return new ColoredDocument();
    }
}
