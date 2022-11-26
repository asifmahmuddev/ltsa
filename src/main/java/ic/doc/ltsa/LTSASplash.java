package ic.doc.ltsa;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class LTSASplash extends Window {
    final Window thisWindow;

    public LTSASplash(Window paramWindow) {
        super(paramWindow);
        this.thisWindow = this;
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("icon/splash.gif"));
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.setLayout(new BorderLayout());
        jPanel.add(new JLabel(imageIcon), "Center");
        jPanel.setBorder(new BevelBorder(0));
        add(jPanel);
        pack();
        Dimension dimension1 = getSize(), dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((dimension2.width - dimension1.width) / 2, (dimension2.height - dimension1.height) / 2, dimension1.width, dimension1.height);
        addMouseListener(new Mouse(this));
        setVisible(true);
    }

    class Mouse extends MouseAdapter {
        private final LTSASplash this$0;

        Mouse(LTSASplash this$0) {
            this.this$0 = this$0;
        }

        public void mouseClicked(MouseEvent param1MouseEvent) {
            this.this$0.thisWindow.setVisible(false);
            this.this$0.thisWindow.dispose();
        }
    }
}
