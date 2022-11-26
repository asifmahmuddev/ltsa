package ic.doc.ltsa;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LTSABlanker extends Window {
    final Window thisWindow;

    public LTSABlanker(Window paramWindow) {
        super(paramWindow);
        this.thisWindow = this;
        setBackground(Color.black);
        pack();
        Dimension dimension1 = getSize(), dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, dimension2.width, dimension2.height);
        addMouseListener(new Mouse(this));
        setVisible(true);
    }

    class Mouse extends MouseAdapter {
        private final LTSABlanker this$0;

        Mouse(LTSABlanker this$0) {
            this.this$0 = this$0;
        }

        public void mouseClicked(MouseEvent param1MouseEvent) {
            this.this$0.thisWindow.setVisible(false);
            this.this$0.thisWindow.dispose();
        }
    }
}
