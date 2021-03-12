/*
 * Decompiled with CFR 0.150.
 */
package gui;

import gui.dialog.JAPDialog;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Component;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClipFrame
extends JAPDialog
implements ActionListener,
ItemListener {
    private TextArea m_TextArea;
    private Choice chooser;
    private ClipChoice[] choices;

    public ClipFrame(Component component, String string, boolean bl, ClipChoice[] arrclipChoice) {
        super(component, string);
        this.init(bl, arrclipChoice);
    }

    public ClipFrame(Component component, String string, boolean bl) {
        super(component, string);
        this.init(bl, null);
    }

    private void init(boolean bl, ClipChoice[] arrclipChoice) {
        this.choices = arrclipChoice;
        if (this.choices == null) {
            this.chooser = null;
        } else {
            this.chooser = new Choice();
            for (int i = 0; i < this.choices.length; ++i) {
                this.chooser.add(this.choices[i].name);
            }
            this.getContentPane().add((Component)this.chooser, "North");
            this.chooser.addItemListener(this);
        }
        this.m_TextArea = new TextArea(30, 80);
        this.m_TextArea.setText("");
        this.getContentPane().add((Component)this.m_TextArea, "Center");
        if (bl) {
            Button button = new Button("Open");
            button.addActionListener(this);
            button.setActionCommand("open");
            this.getContentPane().add((Component)button, "South");
        }
        this.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent windowEvent) {
                ClipFrame.this.dispose();
            }
        });
        this.pack();
    }

    public void setText(String string) {
        this.m_TextArea.setText(string);
    }

    public String getText() {
        return this.m_TextArea.getText();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("open")) {
            if (this.m_TextArea.getText().equals("")) {
                JAPDialog.showErrorDialog((Component)this.getOwner(), "The Text Area is empty!");
            } else {
                this.dispose();
            }
        }
    }

    public void itemStateChanged(ItemEvent itemEvent) {
        this.setText(this.choices[this.chooser.getSelectedIndex()].text);
    }

    public static class ClipChoice {
        public String name;
        public String text;

        public ClipChoice(String string, String string2) {
            this.name = string;
            this.text = string2;
        }
    }
}

