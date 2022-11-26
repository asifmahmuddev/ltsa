package ic.doc.extension;

import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class LTSAButton extends JButton {
    public LTSAButton(ImageIcon paramImageIcon, String paramString, ActionListener paramActionListener) {
        setIcon(paramImageIcon);
        setRequestFocusEnabled(false);
        setMargin(new Insets(0, 0, 0, 0));
        setToolTipText(paramString);
        addActionListener(paramActionListener);
    }

    public float getAlignmentY() {
        return 0.5F;
    }
}
