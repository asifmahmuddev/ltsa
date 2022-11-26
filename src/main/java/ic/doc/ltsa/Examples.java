package ic.doc.ltsa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class Examples {
    JMenu parent;
    HPWindow out;

    public Examples(JMenu paramJMenu, HPWindow paramHPWindow) {
        this.parent = paramJMenu;
        this.out = paramHPWindow;
    }

    public void getExamples() {
        List list = getContents("example/contents.txt");
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            JMenu jMenu = new JMenu(str.substring(0, str.indexOf('_')));
            this.parent.add(jMenu);
            List list1 = getContents("example/" + str + "/contents.txt");
            Iterator iterator1 = list1.iterator();
            while (iterator1.hasNext()) {
                String str1 = iterator1.next();
                int i = str1.indexOf('.');
                String str2 = (i > 0) ? str1.substring(0, i) : str1;
                JMenuItem jMenuItem = new JMenuItem(str2);
                jMenuItem.addActionListener(new ExampleAction(this, "example/" + str + "/", str1));
                jMenu.add(jMenuItem);
            }
        }
    }

    private List getContents(String paramString) {
        ArrayList arrayList = new ArrayList(16);
        try {
            InputStream inputStream = getClass().getResourceAsStream(paramString);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                if (!str.equals(""))
                    arrayList.add(str);
            }
        } catch (Exception exception) {
            this.out.outln("Error getting resource: " + paramString);
        }
        return arrayList;
    }

    class ExampleAction implements ActionListener {
        String dir;
        String ex;
        private final Examples this$0;

        ExampleAction(Examples this$0, String param1String1, String param1String2) {
            this.this$0 = this$0;
            this.dir = param1String1;
            this.ex = param1String2;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.out.newExample(this.dir, this.ex);
        }
    }
}
