package ic.doc.ltsa;

import javax.swing.UIManager;

public class Launcher {
    private static final String[] LINUX_LOOK_AND_FEELS = new String[]{"javax.swing.plaf.nimbus.NimbusLookAndFeel", "javax.swing.plaf.metal.MetalLookAndFeel",
        "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"};

    public static void main(String[] paramArrayOfString) throws Exception {
        String str = System.getProperty("os.name", "").toLowerCase();
        if (str.contains("windows")) {
            System.setProperty("swing.noxp", "true");
        } else if (str.contains("linux")) {
            System.setProperty("javax.accessibility.assistive_technologies", " ");
            selectLookAndFeel();
        }
        HPWindow.main(paramArrayOfString);
    }

    private static void selectLookAndFeel() {
        for (String str : LINUX_LOOK_AND_FEELS) {
            try {
                UIManager.setLookAndFeel(str);
                System.setProperty("swing.systemlaf", str);
                return;
            } catch (Throwable throwable) {
            }
        }
    }
}
