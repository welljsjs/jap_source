/*
 * Decompiled with CFR 0.150.
 */
package jap;

import gui.GUIUtils;
import gui.JAPAboutAutoScroller;
import gui.dialog.JAPDialog;
import jap.JAPAboutNew;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;

final class JAPAbout
extends JAPDialog {
    private static final int ABOUT_DY = 173;
    private static final int ABOUT_DX = 350;
    private JAPAboutAutoScroller sp;

    public JAPAbout(Window window) {
        super(window, "Info...", false);
        super.setVisible(false);
        try {
            this.init();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void init() {
        this.setVisible(false);
        this.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent windowEvent) {
                JAPAbout.this.OKPressed();
            }
        });
        this.setLocation(-380, -200);
        this.setSize(10, 10);
        ImageIcon imageIcon = GUIUtils.loadImageIcon("info.gif", true, false);
        byte[] arrby = JAPAboutNew.loadAboutText().getBytes();
        this.sp = new JAPAboutAutoScroller(350, 173, imageIcon.getImage(), 5, 62, 210, 101, new String(arrby));
        this.sp.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPAbout.this.OKPressed();
            }
        });
        this.getContentPane().setBackground(new Color(204, 204, 204));
        this.getContentPane().setLayout(null);
        this.getContentPane().add(this.sp);
        this.setVisible(true);
        this.setVisible(true);
        this.setResizable(false);
        Insets insets = this.getInsets();
        this.setSize(350 + insets.left + insets.right, 173 + insets.bottom + insets.top);
        this.setResizable(false);
        this.setLocationRelativeTo(this.getOwner(), 0);
        this.toFront();
        this.sp.startScrolling(95);
    }

    private void OKPressed() {
        this.sp.stopScrolling();
        this.dispose();
    }
}

