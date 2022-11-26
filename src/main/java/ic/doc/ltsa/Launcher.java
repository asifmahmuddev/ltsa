package ic.doc.ltsa;

import javax.swing.UIManager;

public class Launcher {
    private static final String[] LINUX_LOOK_AND_FEELS = {
        "javax.swing.plaf.nimbus.NimbusLookAndFeel",
        "javax.swing.plaf.metal.MetalLookAndFeel",
        "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"
    };

    public static void main(String[] args) throws Exception {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("windows")) {
            System.setProperty("swing.noxp", "true");
        } else if (os.contains("linux")) {
            System.setProperty("javax.accessibility.assistive_technologies", " ");
            selectLookAndFeel();
        }

        HPWindow.main(args);
    }

    private static void selectLookAndFeel() {
        for (String laf : LINUX_LOOK_AND_FEELS) {
            try {
                UIManager.setLookAndFeel(laf);
                System.setProperty("swing.systemlaf", laf);
                return;
            } catch (Throwable ignored) {}
        }
    }
}
