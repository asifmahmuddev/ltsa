package gov.nasa.arc.ase.ltl;

import gov.nasa.arc.ase.util.graph.Graph;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LTL2BuchiApplet extends Applet implements ActionListener, ItemListener {
    TextField text;
    Button button;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Checkbox cBox;
    ImagePanel imagePanel;
    TextArea output;
    GridBagLayout gridbag;
    GridBagConstraints c;
    static Frame f;

    protected void addComponent(Component paramComponent, GridBagLayout paramGridBagLayout, GridBagConstraints paramGridBagConstraints) {
        paramGridBagLayout.setConstraints(paramComponent, paramGridBagConstraints);
        add(paramComponent);
    }

    public void init() {
        this.gridbag = new GridBagLayout();
        this.c = new GridBagConstraints();
        setFont(new Font("Helvetica", 0, 14));
        setLayout(this.gridbag);
        this.c.gridwidth = 0;
        Label label = new Label("LTL to Optimized Buchi");
        addComponent(label, this.gridbag, this.c);
        this.c.fill = 1;
        this.c.weightx = 1.0D;
        this.c.weighty = 0.0D;
        this.c.gridheight = 1;
        this.c.gridwidth = 6;
        this.c.gridx = 0;
        this.text = new TextField(50);
        addComponent(this.text, this.gridbag, this.c);
        this.output = new TextArea(20, 20);
        this.output.setEditable(false);
        this.c.weightx = 1.0D;
        this.c.weighty = 1.0D;
        this.c.gridwidth = 2;
        this.c.gridheight = 11;
        this.c.gridx = 0;
        this.c.gridy = 2;
        addComponent(this.output, this.gridbag, this.c);
        this.imagePanel = new ImagePanel();
        this.c.weightx = 1.0D;
        this.c.weighty = 1.0D;
        this.c.gridwidth = 4;
        this.c.gridheight = 11;
        this.c.gridx = 2;
        this.c.gridy = 2;
        addComponent(this.imagePanel, this.gridbag, this.c);
        this.c.gridwidth = 1;
        this.c.gridheight = 1;
        this.c.weightx = 0.0D;
        this.c.weighty = 0.0D;
        this.c.gridx = 6;
        this.c.gridy = 1;
        this.button = new Button("Calculate");
        this.button.addActionListener(this);
        addComponent(this.button, this.gridbag, this.c);
        Panel panel1 = new Panel();
        this.c.gridwidth = 1;
        this.c.gridheight = 1;
        this.c.weightx = 1.0D;
        this.c.weighty = 0.0D;
        this.c.gridx = 2;
        this.c.gridy = 14;
        addComponent(panel1, this.gridbag, this.c);
        this.button6 = new Button("Reduce");
        this.button6.addActionListener(this);
        this.button6.setEnabled(false);
        panel1.add(this.button6);
        this.button7 = new Button("Normal size");
        this.button7.addActionListener(this);
        this.button7.setEnabled(false);
        panel1.add(this.button7);
        this.button8 = new Button("Increase");
        this.button8.addActionListener(this);
        this.button8.setEnabled(false);
        panel1.add(this.button8);
        Panel panel2 = new Panel();
        this.c.gridwidth = 1;
        this.c.gridheight = 11;
        this.c.gridx = 6;
        this.c.gridy = 2;
        this.c.weightx = 0.0D;
        this.c.weighty = 0.0D;
        addComponent(panel2, this.gridbag, this.c);
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        panel2.setFont(new Font("Helvetica", 0, 14));
        panel2.setLayout(gridBagLayout);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0D;
        this.cBox = new Checkbox("Bubble help");
        this.cBox.addItemListener(this);
        this.cBox.setEnabled(false);
        gridBagLayout.setConstraints(this.cBox, gridBagConstraints);
        panel2.add(this.cBox);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.0D;
        this.button1 = new MyButton("Show GEN", f);
        this.button1.addActionListener(this);
        this.button1.setEnabled(false);
        gridBagLayout.setConstraints(this.button1, gridBagConstraints);
        panel2.add(this.button1);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 0.0D;
        this.button2 = new MyButton("Show SSR", f);
        this.button2.addActionListener(this);
        this.button2.setEnabled(false);
        gridBagLayout.setConstraints(this.button2, gridBagConstraints);
        panel2.add(this.button2);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 0.0D;
        this.button3 = new MyButton("Show DEG", f);
        this.button3.addActionListener(this);
        this.button3.setEnabled(false);
        gridBagLayout.setConstraints(this.button3, gridBagConstraints);
        panel2.add(this.button3);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 0.0D;
        this.button4 = new MyButton("Show SCC", f);
        this.button4.addActionListener(this);
        this.button4.setEnabled(false);
        gridBagLayout.setConstraints(this.button4, gridBagConstraints);
        panel2.add(this.button4);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weightx = 0.0D;
        this.button5 = new MyButton("Show SFS", f);
        this.button5.addActionListener(this);
        this.button5.setEnabled(false);
        gridBagLayout.setConstraints(this.button5, gridBagConstraints);
        panel2.add(this.button5);
        this.c.gridwidth = 1;
        this.c.gridheight = 1;
        this.c.gridx = 6;
        this.c.gridy = 14;
        this.c.weightx = 0.0D;
        this.c.weighty = 0.0D;
        this.button = new Button("QUIT");
        this.button.addActionListener(this);
        this.button.setBackground(Color.red);
        addComponent(this.button, this.gridbag, this.c);
    }

    public void actionPerformed(ActionEvent paramActionEvent) {
        String str = paramActionEvent.getActionCommand();
        if (str != null) {
            String str1 = null;
            Object object = null;
            String str2 = null;
            Graphics graphics = this.imagePanel.getGraphics();
            if (str.equals("Reduce")) {
                this.imagePanel.reduce();
                this.imagePanel.repaint();
            } else if (str.equals("Normal size")) {
                this.imagePanel.setNormal();
                this.imagePanel.repaint();
            } else if (str.equals("Increase")) {
                this.imagePanel.increase();
                this.imagePanel.repaint();
            } else {
                Object object1 = null;
                if (str.equals("Calculate")) {
                    try {
                        Runtime runtime = Runtime.getRuntime();
                        String[] arrayOfString = {"cleanup.bat"};
                        Process process = runtime.exec(arrayOfString);
                        process.waitFor();
                    } catch (InterruptedException interruptedException) {
                        System.out.println("Process interrupted: " + interruptedException);
                    } catch (IOException iOException) {
                        System.out.println("Error in execution: " + iOException);
                    }
                    String str3 = this.text.getText();
                    if (str3.equals("")) {
                        if (this.button1.isEnabled()) {
                            this.button1.setEnabled(false);
                            this.button2.setEnabled(false);
                            this.button3.setEnabled(false);
                            this.button4.setEnabled(false);
                            this.button5.setEnabled(false);
                            this.button6.setEnabled(false);
                            this.button7.setEnabled(false);
                            this.button8.setEnabled(false);
                            this.cBox.setEnabled(false);
                        }
                    } else {
                        if (!this.button1.isEnabled()) {
                            this.button1.setEnabled(true);
                            this.button2.setEnabled(true);
                            this.button3.setEnabled(true);
                            this.button4.setEnabled(true);
                            this.button5.setEnabled(true);
                            this.button6.setEnabled(true);
                            this.button7.setEnabled(true);
                            this.button8.setEnabled(true);
                            this.cBox.setEnabled(true);
                        }
                        LTL2Buchi lTL2Buchi = new LTL2Buchi();
                        LTL2Buchi.reset_all_static();
                        try {
                            Graph graph = LTL2Buchi.translate(str3);
                            try {
                                Runtime runtime = Runtime.getRuntime();
                                String[] arrayOfString = {"sm2gif.bat", "fairSim-final.sm", "sfs-ba.gif"};
                                Process process = runtime.exec(arrayOfString);
                                process.waitFor();
                            } catch (InterruptedException interruptedException) {
                                System.out.println("Process interrupted: " + interruptedException);
                            } catch (IOException iOException) {
                                System.out.println("Error in execution: " + iOException);
                            }
                            try {
                                Runtime runtime = Runtime.getRuntime();
                                String[] arrayOfString = {"sm2gif.bat", "gba.sm", "gba.gif"};
                                Process process = runtime.exec(arrayOfString);
                                process.waitFor();
                            } catch (InterruptedException interruptedException) {
                                System.out.println("Process interrupted: " + paramActionEvent);
                            } catch (IOException iOException) {
                                System.out.println("Error in execution: " + iOException);
                            }
                            try {
                                Runtime runtime = Runtime.getRuntime();
                                String[] arrayOfString = {"sm2gif.bat", "ba.sm", "ba.gif"};
                                Process process = runtime.exec(arrayOfString);
                                process.waitFor();
                            } catch (InterruptedException interruptedException) {
                                System.out.println("Process interrupted: " + paramActionEvent);
                            } catch (IOException iOException) {
                                System.out.println("Error in execution: " + iOException);
                            }
                            try {
                                Runtime runtime = Runtime.getRuntime();
                                String[] arrayOfString = {"sm2gif.bat", "ssr-gba.sm", "ssr-gba.gif"};
                                Process process = runtime.exec(arrayOfString);
                                process.waitFor();
                            } catch (InterruptedException interruptedException) {
                                System.out.println("Process interrupted: " + paramActionEvent);
                            } catch (IOException iOException) {
                                System.out.println("Error in execution: " + iOException);
                            }
                            try {
                                Runtime runtime = Runtime.getRuntime();
                                String[] arrayOfString = {"sm2gif.bat", "scc-ba.sm", "scc-ba.gif"};
                                Process process = runtime.exec(arrayOfString);
                                process.waitFor();
                            } catch (InterruptedException interruptedException) {
                                System.out.println("Process interrupted: " + paramActionEvent);
                            } catch (IOException iOException) {
                                System.out.println("Error in execution: " + iOException);
                            }
                            str1 = "sfs-ba.gif";
                            str2 = "sfs-ba.txt";
                        } catch (ParseErrorException parseErrorException) {
                            this.output.setText("Error: \n" + parseErrorException);
                            this.button1.setEnabled(false);
                            this.button2.setEnabled(false);
                            this.button3.setEnabled(false);
                            this.button4.setEnabled(false);
                            this.button5.setEnabled(false);
                            this.button6.setEnabled(false);
                            this.button7.setEnabled(false);
                            this.button8.setEnabled(false);
                            this.cBox.setEnabled(false);
                        }
                    }
                } else if (str.equals("Show GEN")) {
                    str1 = "gba.gif";
                    str2 = "gba.txt";
                } else if (str.equals("Show SSR")) {
                    str1 = "ssr-gba.gif";
                    str2 = "ssr-gba.txt";
                } else if (str.equals("Show DEG")) {
                    str1 = "ba.gif";
                    str2 = "ba.txt";
                } else if (str.equals("Show SCC")) {
                    str1 = "scc-ba.gif";
                    str2 = "scc-ba.txt";
                } else if (str.equals("Show SFS")) {
                    str1 = "sfs-ba.gif";
                    str2 = "sfs-ba.txt";
                } else if (str.equals("QUIT")) {
                    try {
                        Runtime runtime = Runtime.getRuntime();
                        String[] arrayOfString = {"cleanup.bat"};
                        Process process = runtime.exec(arrayOfString);
                        process.waitFor();
                    } catch (InterruptedException interruptedException) {
                        System.out.println("Process interrupted: " + interruptedException);
                    } catch (IOException iOException) {
                        System.out.println("Error in execution: " + iOException);
                    }
                    f.dispose();
                    System.exit(0);
                }
                if (this.button1.isEnabled()) {
                    Image image = Toolkit.getDefaultToolkit().createImage(str1);
                    this.imagePanel.setImage(image);
                    this.imagePanel.repaint();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(str2));
                        this.output.setText(bufferedReader.readLine());
                        for (String str3 = bufferedReader.readLine(); str3 != null; str3 = bufferedReader.readLine())
                            this.output.append("\n" + str3);
                    } catch (FileNotFoundException fileNotFoundException) {
                        System.out.println("Error: " + fileNotFoundException);
                    } catch (IOException iOException) {
                        System.out.println("Error: " + iOException);
                    }
                }
            }
        }
    }

    public void itemStateChanged(ItemEvent paramItemEvent) {
        if (paramItemEvent.getStateChange() == 1) {
            showHelp = true;
        } else {
            showHelp = false;
        }
    }

    public static void main(String[] paramArrayOfString) {
        f = new Frame("LTL to Buchi Translator");
        LTL2BuchiApplet lTL2BuchiApplet = new LTL2BuchiApplet();
        lTL2BuchiApplet.init();
        f.add(lTL2BuchiApplet);
        f.pack();
        f.show();
    }

    public static boolean showHelp = false;
}
