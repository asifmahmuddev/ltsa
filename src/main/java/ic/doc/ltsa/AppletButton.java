package ic.doc.ltsa;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.UIManager;

public class AppletButton extends Applet implements Runnable {
    Button button;
    Thread windowThread;
    boolean pleaseCreate = false;
    HPWindow window = null;

    public void init() {
        setLayout(new BorderLayout());
        this.button = new Button("Launch LTSA");
        this.button.setFont(new Font("Helvetica", 1, 18));
        this.button.addActionListener(new ButtonAction(this));
        add("Center", this.button);
    }

    public void start() {
        if (this.windowThread == null) {
            this.windowThread = new Thread(this);
            this.windowThread.start();
        }
    }

    public void stop() {
        this.windowThread = null;
        if (this.window != null)
            this.window.dispose();
    }

    public void ended() {
        if (this.window != null)
            this.window = null;
    }

    public synchronized void run() {
        while (this.windowThread != null) {
            while (!this.pleaseCreate) {
                try {
                    wait();
                } catch (InterruptedException interruptedException) {
                }
            }
            this.pleaseCreate = false;
            try {
                String str = UIManager.getSystemLookAndFeelClassName();
                UIManager.setLookAndFeel(str);
            } catch (Exception exception) {
            }
            if (this.window == null) {
                showStatus("Please wait while the window comes up...");
                this.window = new HPWindow(this);
                this.window.setTitle("LTS Analyser");
                this.window.pack();
                HPWindow.centre(this.window);
                this.window.setVisible(true);
                showStatus("");
            }
        }
    }

    synchronized void triggerWindow() {
        this.pleaseCreate = true;
        notify();
    }

    class ButtonAction implements ActionListener {
        private final AppletButton this$0;

        ButtonAction(AppletButton this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.triggerWindow();
        }
    }
}
