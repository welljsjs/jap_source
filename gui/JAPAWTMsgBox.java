/*
 * Decompiled with CFR 0.150.
 */
package gui;

import gui.GUIUtils;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.StringTokenizer;

public final class JAPAWTMsgBox
extends WindowAdapter
implements ActionListener {
    private Dialog d;

    private JAPAWTMsgBox(String string, String string2) {
        try {
            Component component;
            Frame frame = new Frame();
            frame.setState(1);
            frame.setVisible(true);
            GUIUtils.setAlwaysOnTop(frame, true);
            this.d = new Dialog(frame, string2, true);
            this.d.addWindowListener(this);
            GridLayout gridLayout = new GridLayout(0, 1, 0, 0);
            Panel panel = new Panel();
            panel.setLayout(gridLayout);
            StringTokenizer stringTokenizer = new StringTokenizer(string, "\n");
            while (stringTokenizer.hasMoreElements()) {
                component = new Label(stringTokenizer.nextToken());
                panel.add(component);
            }
            panel.add(new Label(" "));
            this.d.add("Center", panel);
            component = new Button("   Ok   ");
            ((Button)component).addActionListener(this);
            panel = new Panel();
            panel.add(component);
            this.d.add("South", panel);
            panel = new Panel();
            panel.setSize(7, 7);
            this.d.add("North", panel);
            panel = new Panel();
            panel.setSize(7, 7);
            this.d.add("West", panel);
            panel = new Panel();
            panel.setSize(7, 7);
            this.d.add("East", panel);
            this.d.pack();
            this.d.setResizable(false);
            Dimension dimension = this.d.getToolkit().getScreenSize();
            try {
                Dimension dimension2 = this.d.getSize();
                this.d.setLocation((dimension.width - dimension2.width) / 2, (dimension.height - dimension2.height) / 2);
            }
            catch (Error error) {
                Dimension dimension3 = this.d.size();
                this.d.locate((dimension.width - dimension3.width) / 2, (dimension.height - dimension3.height) / 2);
            }
            this.d.show();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static final int MsgBox(String string, String string2) {
        try {
            JAPAWTMsgBox jAPAWTMsgBox = new JAPAWTMsgBox(string, string2);
        }
        catch (Exception exception) {
            return -1;
        }
        return 0;
    }

    public void windowClosing(WindowEvent windowEvent) {
        this.d.dispose();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        this.d.dispose();
    }
}

